package com.openclassrooms.realestatemanager.modules

import android.content.Context
import android.os.Build
import com.google.android.exoplayer2.ExoPlayerLibraryInfo
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.services.VideoDownloadService
import com.openclassrooms.realestatemanager.services.VideoDownloadServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltVideoDownloadServiceModule {

    @Qualifier
    annotation class UserAgent

    @Provides
    @UserAgent
    fun provideUserAgent(): String{
        return ("ExoPlayerDemo/"
                + ExoPlayerLibraryInfo.VERSION
                + " (Linux; Android "
                + Build.VERSION.RELEASE
                + ") "
                + ExoPlayerLibraryInfo.VERSION_SLASHY)
    }

    @Provides
    fun provideDownloadDirectory(@ApplicationContext context: Context): File{
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
    fun provideDownloadManager(
        @ApplicationContext context: Context,
        downloadCache: Cache,
        databaseProvider: DatabaseProvider,
        @UserAgent userAgent: String
    ): DownloadManager {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory().setUserAgent(userAgent)

        return DownloadManager(
            context,
            databaseProvider,
            downloadCache,
            httpDataSourceFactory,
            Executors.newFixedThreadPool(6)
        )
    }

    @Provides
    @Singleton
    fun provideVideoDownloadService(
        @ApplicationContext context: Context,
        downloadManager: DownloadManager
    ): VideoDownloadService{
        return VideoDownloadServiceImpl(context, downloadManager)
    }
}