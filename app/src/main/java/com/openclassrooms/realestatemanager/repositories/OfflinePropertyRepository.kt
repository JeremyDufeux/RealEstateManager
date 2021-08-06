package com.openclassrooms.realestatemanager.repositories

import android.content.Context
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.offline.*
import com.openclassrooms.realestatemanager.database.PropertyDao
import com.openclassrooms.realestatemanager.mappers.mediaItemToMediaItemEntityMapper
import com.openclassrooms.realestatemanager.mappers.propertyEntityToPropertyMapper
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyEntityMapper
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.databaseEntites.PointOfInterestEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyPointOfInterestCrossRef
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.services.VideoDownloadService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class OfflinePropertyRepository @Inject constructor(
    @ApplicationContext private val mContext: Context,
    private val mVideoDownloadService: VideoDownloadService,
    private val mPropertyDao: PropertyDao) {

    fun getProperties(): Flow<List<Property>> = mPropertyDao.getProperties().map { list ->
        val propertyList = mutableListOf<Property>()
        for (property in list) {
            propertyList.add(propertyEntityToPropertyMapper(property))
        }
        return@map propertyList
    }

    fun getPropertyWithId(propertyId: String): Flow<Property> =
        mPropertyDao.getPropertyWithId(propertyId).map { property ->
            return@map propertyEntityToPropertyMapper(property)
        }

    suspend fun updateDatabase(propertiesList: List<Property>) {
        for (property in propertiesList) {
            val propertyEntity = propertyToPropertyEntityMapper(property)
            mPropertyDao.insertProperty(propertyEntity)

            cachePicture(property.mapPictureUrl)

            for (media in property.mediaList) {
                val mediaItemEntity = mediaItemToMediaItemEntityMapper(property.id, media)
                mPropertyDao.insertMediaItem(mediaItemEntity)

                cachePicture(media.url)

                if (media.fileType == FileType.VIDEO) {
                    cacheVideo(media)
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
    }

    private fun cachePicture(url: String?) {
        Glide.with(mContext).load(url).submit()
    }

    private fun cacheVideo(mediaItem: MediaItem) {
        mVideoDownloadService.cacheVideo(mediaItem)
    }
}