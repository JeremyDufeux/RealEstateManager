package com.openclassrooms.realestatemanager.modules

import com.openclassrooms.realestatemanager.services.EstateApiService
import com.openclassrooms.realestatemanager.services.EstateApiServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class EstateApiServiceModule{

    @Provides
    fun provideEstateApiService() : EstateApiService {
        return EstateApiServiceImpl()
    }
}