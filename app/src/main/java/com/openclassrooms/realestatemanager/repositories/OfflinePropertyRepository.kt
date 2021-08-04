package com.openclassrooms.realestatemanager.repositories

import android.content.Context
import android.net.Uri
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.offline.*
import com.openclassrooms.realestatemanager.database.PropertyDao
import com.openclassrooms.realestatemanager.mappers.MediaItemToMediaItemEntityMapper
import com.openclassrooms.realestatemanager.mappers.PropertyEntityToPropertyMapper
import com.openclassrooms.realestatemanager.mappers.PropertyToPropertyEntityMapper
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
    private val mPropertyDao: PropertyDao) {

    fun getProperties(): Flow<List<Property>> = mPropertyDao.getProperties().map { list ->
        val propertyList = mutableListOf<Property>()
        for (property in list) {
            propertyList.add(PropertyEntityToPropertyMapper.map(property))
        }
        return@map propertyList
    }

    fun getPropertyWithId(propertyId: String): Flow<Property> =
        mPropertyDao.getPropertyWithId(propertyId).map { property ->
            return@map PropertyEntityToPropertyMapper.map(property)
        }

    suspend fun updateDatabase(propertiesList: List<Property>) {
        for (property in propertiesList) {
            val propertyEntity = PropertyToPropertyEntityMapper.map(property)
            mPropertyDao.insertProperty(propertyEntity)

            for (media in property.mediaList) {
                val mediaItemEntity = MediaItemToMediaItemEntityMapper.map(property.id, media)
                mPropertyDao.insertMediaItem(mediaItemEntity)

                cachePicture(media.url)

                if (media.fileType == FileType.VIDEO) {
                    cacheVideo(media)
                }
            }

            DownloadService.start(
                mContext,
                VideoDownloadService::class.java
            )

            for (pointOfInterest in property.pointOfInterestList) {
                val pointOfInterestEntity = PointOfInterestEntity(pointOfInterest.toString())
                mPropertyDao.insertPointOfInterest(pointOfInterestEntity)

                val crossRef =
                    PropertyPointOfInterestCrossRef(property.id, pointOfInterest.toString())
                mPropertyDao.insertPropertyPointOfInterestCrossRef(crossRef)
            }
        }
    }

    private fun cachePicture(url: String) {
        Glide.with(mContext).load(url).submit()
    }

    private fun cacheVideo(mediaItem: MediaItem) {
        val uri = Uri.parse(mediaItem.url)
        val downloadRequest = DownloadRequest.Builder(mediaItem.id, uri).build()

        DownloadService.sendAddDownload(
            mContext,
            VideoDownloadService::class.java,
            downloadRequest,
            true
        )
    }
}