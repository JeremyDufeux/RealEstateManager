package com.openclassrooms.realestatemanager.modules

import com.openclassrooms.realestatemanager.api.PropertyApiService
import com.openclassrooms.realestatemanager.api.PropertyApiServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltPropertyApiServiceModule {

    @Provides
    @Singleton
    fun providePropertyApiService(): PropertyApiService{
        return PropertyApiServiceImpl()
    }
}