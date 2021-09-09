package com.openclassrooms.realestatemanager.database

import androidx.annotation.VisibleForTesting
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.models.databaseEntites.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

    @Transaction
    @Query("SELECT * FROM properties ORDER BY propertyId")
    suspend fun getProperties(): List<PropertyWithMediaItemAndPointsOfInterestEntity>

    @Transaction
    @Query("SELECT * FROM properties WHERE propertyId=:propertyId")
    suspend fun getPropertyWithId(propertyId: String): PropertyWithMediaItemAndPointsOfInterestEntity?

    @Transaction
    @Query("SELECT * FROM properties WHERE propertyId=:propertyId")
    fun getPropertyWithIdFlow(propertyId: String): Flow<PropertyWithMediaItemAndPointsOfInterestEntity>

    @Transaction
    @Query("SELECT * FROM properties WHERE dataState='WAITING_UPLOAD'")
    suspend fun getPropertiesToUpload(): List<PropertyWithMediaItemAndPointsOfInterestEntity>

    @Transaction
    @Query("SELECT * FROM media_items WHERE dataState='WAITING_UPLOAD'")
    suspend fun getMediaItemsToUpload(): List<MediaItemEntity>

    @Transaction
    @Query("SELECT * FROM media_items WHERE dataState='WAITING_DELETE'")
    suspend fun getMediaItemsToDelete(): List<MediaItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(mediaItem: MediaItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointOfInterest(pointOfInterest: PointOfInterestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPropertyPointOfInterestCrossRef(propertyPointOfInterestCrossRef: PropertyPointOfInterestCrossRef)

    @Query("DELETE FROM properties_point_of_interest_cross_ref WHERE propertyId=:id")
    suspend fun deletePointsOfInterestForProperty(id: String)

    @Query("DELETE FROM media_items WHERE mediaId=:id")
    suspend fun deleteMediaWithId(id: String)

    @Query("DELETE FROM properties WHERE dataState='OLD'")
    suspend fun deleteOldProperties()

    @Query("DELETE FROM media_items WHERE dataState='OLD'")
    suspend fun deleteOldMedias()

    @Query("DELETE FROM properties_point_of_interest_cross_ref WHERE dataState='OLD'")
    suspend fun deleteOldPointsOfInterest()

    @Query("UPDATE properties SET dataState='OLD'")
    suspend fun updatePropertiesToOld()

    @Query("UPDATE media_items SET dataState='OLD'")
    suspend fun updateMediasToOld()

    @Query("UPDATE properties_point_of_interest_cross_ref SET dataState='OLD'")
    suspend fun updatePointsOfInterestToOld()

    @RawQuery
    suspend fun getPropertyWithFilters(query: SimpleSQLiteQuery): List<PropertyWithMediaItemAndPointsOfInterestEntity>

    // For testing

    @VisibleForTesting
    @Query("SELECT * FROM media_items")
    suspend fun getMedias(): List<MediaItemEntity>

    @VisibleForTesting
    @Query("SELECT * FROM points_of_interest")
    suspend fun getPointsOfInterest(): List<PointOfInterestEntity>

    @VisibleForTesting
    @Query("SELECT * FROM properties_point_of_interest_cross_ref")
    suspend fun getPointsOfInterestCrossRef(): List<PropertyPointOfInterestCrossRef>
}