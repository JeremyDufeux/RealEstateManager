package com.openclassrooms.realestatemanager.modules

import android.content.Context
import com.openclassrooms.realestatemanager.mappers.PropertyToPropertyUiDetailsViewMapper
import com.openclassrooms.realestatemanager.mappers.PropertyToPropertyUiListViewMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class MappersModule {
    @Provides
    fun providePropertyToPropertyUiListViewMapper(@ApplicationContext context: Context) : PropertyToPropertyUiListViewMapper {
        return PropertyToPropertyUiListViewMapper(context)
    }

    @Provides
    fun providePropertyToPropertyUiDetailsViewMapper(@ApplicationContext context: Context) : PropertyToPropertyUiDetailsViewMapper {
        return PropertyToPropertyUiDetailsViewMapper(context)
    }
}