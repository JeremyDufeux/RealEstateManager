package com.openclassrooms.realestatemanager.modules

import android.content.Context
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.services.FakeVideoDownloaderService
import com.openclassrooms.realestatemanager.services.VideoDownloadService
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import java.io.File
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [HiltVideoDownloadServiceModule::class]
)
class FakeVideoDownloaderServiceModule {

    @Provides
    fun provideDownloadDirectory(@ApplicationContext context: Context): File {
        return File(context.getExternalFilesDir(null), context.getString(R.string.app_name))
    }

    @Provides
    @Singleton
    fun provideDatabaseProvider(@ApplicationContext context: Context): DatabaseProvider {
        return ExoDatabaseProvider(context)
    }

    @Provides
    @Singleton
    fun provideUserDownloadCache(downloadDirectory: File, databaseProvider: DatabaseProvider): Cache {
        return SimpleCache(downloadDirectory, NoOpCacheEvictor(), databaseProvider)
    }

    @Provides
    @Singleton
    fun provideVideoDownloadService(): VideoDownloadService {
        return FakeVideoDownloaderService()
    }
}