package com.openclassrooms.realestatemanager.modules

import android.content.Context
import com.openclassrooms.realestatemanager.services.ImageSaver
import com.openclassrooms.realestatemanager.services.OrientationService
import com.openclassrooms.realestatemanager.services.VideoRecorder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class HiltViewModelModules {
    @Provides
    fun provideOrientationService(@ApplicationContext context: Context) : OrientationService {
        return OrientationService(context)
    }

    @Provides
    fun provideImageSaver(@ApplicationContext context: Context) : ImageSaver {
        return ImageSaver(context)
    }

    @Provides
    fun provideVideoRecorder(@ApplicationContext context: Context) : VideoRecorder {
        return VideoRecorder(context)
    }
}