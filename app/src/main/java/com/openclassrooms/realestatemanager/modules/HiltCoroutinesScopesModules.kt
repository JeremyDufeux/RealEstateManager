package com.openclassrooms.realestatemanager.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class DefaultCoroutineScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IoCoroutineScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class MainCoroutineScope

@InstallIn(SingletonComponent::class)
@Module
object HiltCoroutinesScopesModules {

    @Singleton
    @DefaultCoroutineScope
    @Provides
    fun providesDefaultCoroutineScope(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): CoroutineScope{
        return CoroutineScope(SupervisorJob() + defaultDispatcher)
    }

    @Singleton
    @IoCoroutineScope
    @Provides
    fun providesIoCoroutineScope(
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): CoroutineScope{
        return CoroutineScope(SupervisorJob() + ioDispatcher)
    }

    @Singleton
    @MainCoroutineScope
    @Provides
    fun providesMainCoroutineScope(
        @MainDispatcher mainDispatcher: CoroutineDispatcher
    ): CoroutineScope{
        return CoroutineScope(SupervisorJob() + mainDispatcher)
    }
}