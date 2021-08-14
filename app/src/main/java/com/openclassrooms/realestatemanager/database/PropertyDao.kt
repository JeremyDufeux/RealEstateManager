package com.openclassrooms.realestatemanager.database

import androidx.room.*
import com.openclassrooms.realestatemanager.models.databaseEntites.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

    @Transaction
    @Query("SELECT * FROM properties ORDER BY propertyId")
    fun getProperties(): List<PropertyWithMediaItemAndPointsOfInterestEntity>

    @Transaction
    @Query("SELECT * FROM properties WHERE propertyId=:propertyId")
    fun getPropertyWithId(propertyId: String): PropertyWithMediaItemAndPointsOfInterestEntity?

    @Transaction
    @Query("SELECT * FROM properties WHERE propertyId=:propertyId")
    fun getPropertyWithIdFlow(propertyId: String): Flow<PropertyWithMediaItemAndPointsOfInterestEntity>

    @Transaction
    @Query("SELECT * FROM properties WHERE dataState='WAITING_UPLOAD'")
    fun getPropertiesToUpload(): List<PropertyWithMediaItemAndPointsOfInterestEntity>

    @Transaction
    @Query("SELECT * FROM media_items WHERE dataState='WAITING_UPLOAD'")
    fun getMediaItemsToUpload(): List<MediaItemEntity>

    @Transaction
    @Query("SELECT * FROM media_items WHERE dataState='WAITING_DELETE'")
    fun getMediaItemsToDelete(): List<MediaItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(mediaItem: MediaItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointOfInterest(pointOfInterest: PointOfInterestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPropertyPointOfInterestCrossRef(propertyPointOfInterestCrossRef: PropertyPointOfInterestCrossRef)

    @Query("DELETE FROM properties_point_of_interest_cross_ref WHERE propertyId=:id")
    fun deletePointsOfInterestForProperty(id: String)

    @Query("DELETE FROM properties_point_of_interest_cross_ref ")
    fun deletePointsOfInterestForAllProperties()

    @Query("DELETE FROM media_items WHERE mediaId=:id")
    fun deleteMediaWithId(id: String)

    @Query("DELETE FROM properties WHERE dataState='OLD'")
    fun deleteOldProperties()

    @Query("DELETE FROM media_items WHERE dataState='OLD'")
    fun deleteOldMedias()
    
    @Query("UPDATE properties SET dataState='OLD'")
    fun updatePropertiesToOld()

    @Query("UPDATE media_items SET dataState='OLD'")
    fun updateMediasToOld()
}