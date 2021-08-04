package com.openclassrooms.realestatemanager.modules

import android.content.Context
import androidx.room.Room
import com.openclassrooms.realestatemanager.database.AppDatabase
import com.openclassrooms.realestatemanager.database.PropertyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class HiltDatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "RealEstateManager.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePropertyDao(database: AppDatabase): PropertyDao {
        return database.propertyDao()
    }
}