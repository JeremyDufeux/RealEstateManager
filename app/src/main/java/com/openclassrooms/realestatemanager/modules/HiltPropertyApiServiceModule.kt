package com.openclassrooms.realestatemanager.modules

import com.openclassrooms.realestatemanager.api.PropertyApiService
import com.openclassrooms.realestatemanager.api.PropertyApiServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class HiltPropertyApiServiceModule {

    @Provides
    fun providePropertyApiService(): PropertyApiService{
        return PropertyApiServiceImpl()
    }
}