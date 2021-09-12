package com.openclassrooms.realestatemanager

import android.content.ContentValues
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.database.AppDatabase
import com.openclassrooms.realestatemanager.database.PropertyDao
import com.openclassrooms.realestatemanager.database.PropertyProviderDao
import com.openclassrooms.realestatemanager.mappers.mediaItemToMediaItemEntityMapper
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyEntityMapper
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyPointOfInterestCrossRef
import com.openclassrooms.realestatemanager.models.enums.DataState
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.modules.HiltDatabaseModule
import com.openclassrooms.realestatemanager.utils.generateProperties
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [HiltDatabaseModule::class]
)
class HiltFakeDatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java)
            .allowMainThreadQueries()
            .addCallback(prepopulateDatabase())
            .build()
    }

    @Provides
    fun providePropertyDao(database: AppDatabase): PropertyDao {
        return database.propertyDao()
    }

    @Provides
    fun providePropertyProviderDao(database: AppDatabase): PropertyProviderDao {
        return database.propertyProviderDao()
    }

    private fun prepopulateDatabase(): RoomDatabase.Callback {
        return object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                val properties = generateProperties()
                val contentValues = ContentValues()

                for(property in properties){
                    val propertyEntity = propertyToPropertyEntityMapper(property, DataState.SERVER)
                    contentValues.put("propertyId", propertyEntity.propertyId)
                    contentValues.put("type", propertyEntity.type.toString())
                    contentValues.put("price", propertyEntity.price)
                    contentValues.put("surface", propertyEntity.surface)
                    contentValues.put("roomsAmount", propertyEntity.roomsAmount)
                    contentValues.put("bathroomsAmount", propertyEntity.bathroomsAmount)
                    contentValues.put("bedroomsAmount", propertyEntity.bedroomsAmount)
                    contentValues.put("description", propertyEntity.description)
                    contentValues.put("addressLine1", propertyEntity.addressLine1)
                    contentValues.put("addressLine2", propertyEntity.addressLine2)
                    contentValues.put("city", propertyEntity.city)
                    contentValues.put("postalCode", propertyEntity.postalCode)
                    contentValues.put("country", propertyEntity.country)
                    contentValues.put("latitude", propertyEntity.latitude)
                    contentValues.put("longitude", propertyEntity.longitude)
                    contentValues.put("mapPictureUrl", propertyEntity.mapPictureUrl)
                    contentValues.put("postDate", propertyEntity.postDate)
                    contentValues.put("agentName", propertyEntity.agentName)
                    contentValues.put("dataState", DataState.NONE.toString())

                    if(propertyEntity.soldDate == null){
                        contentValues.putNull("soldDate")
                    } else {
                        contentValues.put("soldDate", propertyEntity.soldDate)
                    }

                    db.insert("properties", OnConflictStrategy.IGNORE, contentValues)
                    contentValues.clear()

                    for (media in property.mediaList){
                        val mediaItemEntity = mediaItemToMediaItemEntityMapper(media)
                        contentValues.put("mediaId", mediaItemEntity.mediaId)
                        contentValues.put("propertyId", mediaItemEntity.propertyId)
                        contentValues.put("description", mediaItemEntity.description)
                        contentValues.put("url", mediaItemEntity.url)
                        contentValues.put("fileType", mediaItemEntity.fileType.toString())
                        contentValues.put("dataState", mediaItemEntity.dataState.toString())

                        db.insert("media_items", OnConflictStrategy.IGNORE, contentValues)
                        contentValues.clear()
                    }

                    for (poi in property.pointOfInterestList){
                        val crossRef =
                            PropertyPointOfInterestCrossRef(property.id, poi.toString(), DataState.NONE)
                        contentValues.put("propertyId", crossRef.propertyId)
                        contentValues.put("description", crossRef.description)
                        contentValues.put("dataState", crossRef.dataState.toString())
                        db.insert("properties_point_of_interest_cross_ref", OnConflictStrategy.IGNORE, contentValues)
                        contentValues.clear()
                    }
                }

                for (poi in PointOfInterest.values()){
                    contentValues.put("description", poi.toString())
                    db.insert("points_of_interest", OnConflictStrategy.IGNORE, contentValues)
                    contentValues.clear()
                }
            }
        }
    }
}