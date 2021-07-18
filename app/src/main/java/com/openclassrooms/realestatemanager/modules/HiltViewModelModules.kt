package com.openclassrooms.realestatemanager.modules

import android.content.Context
import com.openclassrooms.realestatemanager.services.OrientationService
import com.openclassrooms.realestatemanager.services.PictureSaver
import com.openclassrooms.realestatemanager.services.VideoRecorder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ViewModelComponent::class)
class HiltViewModelModules {
    @Provides
    fun provideOrientationService(@ApplicationContext context: Context) : OrientationService {
        return OrientationService(context)
    }

    @Provides
    fun provideImageSaver(
        @DefaultCoroutineScope defaultScope: CoroutineScope,
        @ApplicationContext context: Context) : PictureSaver {
        return PictureSaver(defaultScope, context)
    }

    @Provides
    fun provideVideoRecorder(@ApplicationContext context: Context) : VideoRecorder {
        return VideoRecorder(context)
    }
}