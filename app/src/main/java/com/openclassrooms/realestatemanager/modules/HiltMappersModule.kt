package com.openclassrooms.realestatemanager.modules

import android.content.Context
import com.openclassrooms.realestatemanager.mappers.PropertyToPropertyUiDetailsViewMapper
import com.openclassrooms.realestatemanager.mappers.PropertyToPropertyUiListViewMapper
import com.openclassrooms.realestatemanager.mappers.PropertyToPropertyUiMapViewMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class HiltMappersModule {
    @Provides
    fun providePropertyToPropertyUiListViewMapper(@ApplicationContext context: Context) : PropertyToPropertyUiListViewMapper {
        return PropertyToPropertyUiListViewMapper(context)
    }

    @Provides
    fun providePropertyToPropertyUiMapViewMapper(@ApplicationContext context: Context) : PropertyToPropertyUiMapViewMapper {
        return PropertyToPropertyUiMapViewMapper(context)
    }

    @Provides
    fun providePropertyToPropertyUiDetailsViewMapper(@ApplicationContext context: Context) : PropertyToPropertyUiDetailsViewMapper {
        return PropertyToPropertyUiDetailsViewMapper(context)
    }
}