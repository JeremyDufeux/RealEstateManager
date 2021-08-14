package com.openclassrooms.realestatemanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanager.models.databaseEntites.MediaItemEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PointOfInterestEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyPointOfInterestCrossRef

@Database(entities = [
    PropertyEntity::class,
    MediaItemEntity::class,
    PointOfInterestEntity::class,
    PropertyPointOfInterestCrossRef::class],
    version = 13, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
}