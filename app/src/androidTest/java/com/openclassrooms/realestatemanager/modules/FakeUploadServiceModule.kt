package com.openclassrooms.realestatemanager.modules

import com.openclassrooms.realestatemanager.worker.FakeUploadService
import com.openclassrooms.realestatemanager.workers.UploadService
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [HiltUploadServiceModule::class]
)
class FakeUploadServiceModule {

    @Provides
    @Singleton
    fun provideFakeUploadService(): UploadService {
        return FakeUploadService()
    }
}
