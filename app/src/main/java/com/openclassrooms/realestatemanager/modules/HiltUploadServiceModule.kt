package com.openclassrooms.realestatemanager.modules

import android.content.Context
import com.openclassrooms.realestatemanager.workers.UploadService
import com.openclassrooms.realestatemanager.workers.UploadServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltUploadServiceModule {

    @Provides
    @Singleton
    fun providePropertyApiService(@ApplicationContext context: Context): UploadService{
        return UploadServiceImpl(context)
    }
}