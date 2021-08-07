package com.openclassrooms.realestatemanager.repositories

import android.content.Context
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.database.PropertyDao
import com.openclassrooms.realestatemanager.mappers.mediaItemToMediaItemEntityMapper
import com.openclassrooms.realestatemanager.mappers.mediaItemsEntityToMediaItemsMapper
import com.openclassrooms.realestatemanager.mappers.propertyEntityToPropertyMapper
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyEntityMapper
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.databaseEntites.PointOfInterestEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyPointOfInterestCrossRef
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.models.enums.ServerState
import com.openclassrooms.realestatemanager.services.VideoDownloadService
import dagger.hilt.android.qualifiers.ApplicationContext
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

    fun getPropertyWithId(propertyId: String): Property =
        mPropertyDao.getPropertyWithId(propertyId).let { property ->
            return propertyEntityToPropertyMapper(property)
        }

    suspend fun updateDatabase(propertiesList: List<Property>) {
        clearDatabase()

        for (property in propertiesList) {
            addProperty(property, ServerState.SERVER)
        }
    }

    private fun clearDatabase() {
        mPropertyDao.deleteAllProperties()
        mPropertyDao.deleteAllMediaItems()
        mPropertyDao.deleteAllPointsOfInterest()
        mPropertyDao.deleteAllPropertiesPointOfInterestCrossRef()
    }

    private fun cachePicture(url: String?) {
        Glide.with(mContext).load(url).submit()
    }

    private fun cacheVideo(mediaItem: MediaItem) {
        mVideoDownloadService.cacheVideo(mediaItem)
    }

    suspend fun addProperty(property: Property, serverState: ServerState) {
        val propertyEntity = propertyToPropertyEntityMapper(property)
        propertyEntity.serverState = serverState
        mPropertyDao.insertProperty(propertyEntity)

        cachePicture(property.mapPictureUrl)

        for (media in property.mediaList) {
            val mediaItemEntity = mediaItemToMediaItemEntityMapper(property.id, media)
            mediaItemEntity.serverState = serverState
            mPropertyDao.insertMediaItem(mediaItemEntity)

            if(serverState == ServerState.SERVER) {
                cachePicture(media.url)

                if (media.fileType == FileType.VIDEO) {
                    cacheVideo(media)
                }
            }
        }

        mVideoDownloadService.startDownloads()

        for (pointOfInterest in property.pointOfInterestList) {
            val pointOfInterestEntity = PointOfInterestEntity(pointOfInterest.toString())
            mPropertyDao.insertPointOfInterest(pointOfInterestEntity)

            val crossRef =
                PropertyPointOfInterestCrossRef(property.id, pointOfInterest.toString())
            mPropertyDao.insertPropertyPointOfInterestCrossRef(crossRef)
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
}