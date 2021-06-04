package com.openclassrooms.realestatemanager.modules

import com.openclassrooms.realestatemanager.services.PropertyApiService
import com.openclassrooms.realestatemanager.services.PropertyApiServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class PropertyApiServiceModule{

    @Provides
    fun providePropertyApiService() : PropertyApiService {
        return PropertyApiServiceImpl()
    }
}