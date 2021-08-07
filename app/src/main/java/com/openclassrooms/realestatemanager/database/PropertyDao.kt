package com.openclassrooms.realestatemanager.database

import androidx.room.*
import com.openclassrooms.realestatemanager.models.databaseEntites.*

@Dao
interface PropertyDao {

    @Transaction
    @Query("SELECT * FROM properties ORDER BY propertyId")
    fun getProperties(): List<PropertyWithMediaItemAndPointsOfInterestEntity>

    @Transaction
    @Query("SELECT * FROM properties WHERE propertyId=:propertyId")
    fun getPropertyWithId(propertyId: String): PropertyWithMediaItemAndPointsOfInterestEntity

    @Transaction
    @Query("SELECT * FROM properties WHERE serverState='WAITING_UPLOAD'")
    fun getPropertiesToUpload(): List<PropertyWithMediaItemAndPointsOfInterestEntity>

    @Transaction
    @Query("SELECT * FROM media_items WHERE serverState='WAITING_UPLOAD'")
    fun getMediaItemsToUpload(): List<MediaItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(mediaItem: MediaItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointOfInterest(pointOfInterest: PointOfInterestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPropertyPointOfInterestCrossRef(propertyPointOfInterestCrossRef: PropertyPointOfInterestCrossRef)

    @Query("DELETE FROM properties")
    fun deleteAllProperties()

    @Query("DELETE FROM media_items")
    fun deleteAllMediaItems()

    @Query("DELETE FROM points_of_interest")
    fun deleteAllPointsOfInterest()

    @Query("DELETE FROM properties_point_of_interest_cross_ref")
    fun deleteAllPropertiesPointOfInterestCrossRef()
}