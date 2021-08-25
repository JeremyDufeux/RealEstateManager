package com.openclassrooms.realestatemanager.repositories

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.database.PropertyDao
import com.openclassrooms.realestatemanager.database.constructSqlQuery
import com.openclassrooms.realestatemanager.mappers.mediaItemToMediaItemEntityMapper
import com.openclassrooms.realestatemanager.mappers.mediaItemsEntityToMediaItemsMapper
import com.openclassrooms.realestatemanager.mappers.propertyEntityToPropertyMapper
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyEntityMapper
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.PropertyFilter
import com.openclassrooms.realestatemanager.models.databaseEntites.PointOfInterestEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyPointOfInterestCrossRef
import com.openclassrooms.realestatemanager.models.enums.DataState
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.services.VideoDownloadService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class OfflinePropertyRepository @Inject constructor(
    @ApplicationContext private val mContext: Context,
    private val mVideoDownloadService: VideoDownloadService,
    private val mPropertyDao: PropertyDao) {

    fun getProperties(): List<Property> = mPropertyDao.getProperties().let { list ->
        val propertyList = mutableListOf<Property>()
        for (property in list) {
            propertyList.add(propertyEntityToPropertyMapper(property))
        }
        return propertyList
    }

    fun getPropertyWithId(propertyId: String): Property?{
        val propertyEntity = mPropertyDao.getPropertyWithId(propertyId)

        return propertyEntity?.let { propertyEntityToPropertyMapper(it) }
    }

    fun getPropertyWithIdFlow(propertyId: String): Flow<Property> =
        mPropertyDao.getPropertyWithIdFlow(propertyId).map { property ->
            return@map propertyEntityToPropertyMapper(property)
        }

    suspend fun updateDatabase(propertiesList: List<Property>) {
        prepareDatabaseBeforeUpdate()
        for (property in propertiesList) {
            addProperty(property, DataState.SERVER)
        }
        deleteOldData()
    }

    private fun prepareDatabaseBeforeUpdate() {
        mPropertyDao.updatePropertiesToOld()
        mPropertyDao.updateMediasToOld()
        mPropertyDao.updatePointsOfInterestToOld()
    }

    private fun deleteOldData() {
        mPropertyDao.deleteOldProperties()
        mPropertyDao.deleteOldMedias()
        mPropertyDao.deleteOldPointsOfInterest()
    }

    private fun cachePicture(url: String?) {
        Glide.with(mContext).load(url).submit()
    }

    private fun cacheVideo(mediaItem: MediaItem) {
        mVideoDownloadService.cacheVideo(mediaItem)
    }

    suspend fun addProperty(property: Property, dataState: DataState) {
        val propertyEntity = propertyToPropertyEntityMapper(property)
        propertyEntity.dataState = dataState
        mPropertyDao.insertProperty(propertyEntity)

        cachePicture(property.mapPictureUrl)

        for (media in property.mediaList) {
            val mediaItemEntity = mediaItemToMediaItemEntityMapper(media)
            mediaItemEntity.dataState = dataState
            mPropertyDao.insertMediaItem(mediaItemEntity)

            if(dataState == DataState.SERVER) {
                cachePicture(media.url)

                if (media.fileType == FileType.VIDEO) {
                    cacheVideo(media)
                }
            }
        }

        startDownloadService()

        insertPointsOfInterestForProperty(property, DataState.SERVER)
    }

    private fun startDownloadService() {
        try {
            mVideoDownloadService.startDownloads()
        } catch (e: Exception){
            Timber.e("Error OfflinePropertyRepository.startDownloadService : ${e.message}")
        }
    }

    fun getPropertiesToUpload(): List<Property> {
        val propertyList = mutableListOf<Property>()
        for (property in mPropertyDao.getPropertiesToUpload()) {
            propertyList.add(propertyEntityToPropertyMapper(property))
        }
        return propertyList
    }

    fun getMediaItemsToUpload(): List<MediaItem> {
        return mediaItemsEntityToMediaItemsMapper(mPropertyDao.getMediaItemsToUpload())
    }

    fun getMediaItemsToDelete(): List<MediaItem> {
        return mediaItemsEntityToMediaItemsMapper(mPropertyDao.getMediaItemsToDelete())
    }

    suspend fun addMediaToUpload(mediaItem: MediaItem) {
        val mediaItemEntity = mediaItemToMediaItemEntityMapper(mediaItem)
        mediaItemEntity.dataState = DataState.WAITING_UPLOAD
        mPropertyDao.insertMediaItem(mediaItemEntity)
    }

    suspend fun setMediaToDelete(mediaItem: MediaItem) {
        val mediaItemEntity = mediaItemToMediaItemEntityMapper(mediaItem)
        mediaItemEntity.dataState = DataState.WAITING_DELETE
        mPropertyDao.insertMediaItem(mediaItemEntity)
    }

    fun deleteMedia(mediaItem: MediaItem) {
        mPropertyDao.deleteMediaWithId(mediaItem.id)

        if(mediaItem.fileType == FileType.VIDEO){
            mVideoDownloadService.deleteVideo(mediaItem)
        }
    }

    suspend fun updateProperty(property: Property) {
        val propertyEntity = propertyToPropertyEntityMapper(property)
        propertyEntity.dataState = DataState.WAITING_UPLOAD

        mPropertyDao.insertProperty(propertyEntity)

        mPropertyDao.deletePointsOfInterestForProperty(property.id)
        insertPointsOfInterestForProperty(property, DataState.NONE)
    }

    private suspend fun insertPointsOfInterestForProperty(property: Property, dataState: DataState){
        for (pointOfInterest in property.pointOfInterestList) {
            val pointOfInterestEntity = PointOfInterestEntity(pointOfInterest.toString())
            mPropertyDao.insertPointOfInterest(pointOfInterestEntity)

            val crossRef =
                PropertyPointOfInterestCrossRef(property.id, pointOfInterest.toString(), dataState)
            mPropertyDao.insertPropertyPointOfInterestCrossRef(crossRef)
        }
    }

    fun getPropertyWithFilters(propertyFilter: PropertyFilter) : List<Property> {
        val queryString = constructSqlQuery(propertyFilter)

        val query = SimpleSQLiteQuery(queryString)

        mPropertyDao.getPropertyWithFilters(query).let{ list ->
            val propertyList = mutableListOf<Property>()
            for (property in list) {
                propertyList.add(propertyEntityToPropertyMapper(property))
            }
            return propertyList
        }
    }
}