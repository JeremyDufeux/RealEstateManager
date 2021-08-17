package com.openclassrooms.realestatemanager.database

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query

@Dao
interface PropertyProviderDao {

    @Query("SELECT * FROM properties ORDER BY propertyId")
    fun getProperties(): Cursor

    @Query("SELECT * FROM properties WHERE propertyId=:propertyId")
    fun getPropertyWithId(propertyId: String?): Cursor?

    @Query("SELECT * FROM media_items")
    fun getMedias(): Cursor

    @Query("SELECT * FROM media_items WHERE mediaId=:mediaId")
    fun getMediaWithId(mediaId: String?): Cursor?

    @Query("SELECT * FROM media_items WHERE propertyId=:propertyId")
    fun getMediaForPropertyId(propertyId: String?): Cursor?

    @Query("SELECT * FROM points_of_interest")
    fun getPointsOfInterest(): Cursor

    @Query("SELECT * FROM properties_point_of_interest_cross_ref WHERE propertyId=:propertyId")
    fun getPointOfInterestForProperty(propertyId: String?): Cursor?
}