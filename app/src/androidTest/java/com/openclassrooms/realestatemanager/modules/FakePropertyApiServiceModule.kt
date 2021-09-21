package com.openclassrooms.realestatemanager.modules

import com.openclassrooms.realestatemanager.api.FakePropertyApiService
import com.openclassrooms.realestatemanager.api.PropertyApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [HiltPropertyApiServiceModule::class]
)
class FakePropertyApiServiceModule {

    @Provides
    @Singleton
    fun provideFakePropertyApiService(): PropertyApiService {
        return FakePropertyApiService()
    }
}