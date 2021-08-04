package com.openclassrooms.realestatemanager

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
import java.io.File
import java.util.concurrent.Executors

class AppContainer(mContext: Context) {
    private val mDatabaseProvider = ExoDatabaseProvider(mContext)

    private  var mDataBase: DatabaseProvider = ExoDatabaseProvider(mContext)

    private var mDownloadContentDirectory: File = File(mContext.getExternalFilesDir(null), mContext.getString(R.string.app_name))

    var mDownloadCache: Cache = SimpleCache(mDownloadContentDirectory, NoOpCacheEvictor(), mDataBase)

    val userAgent = ("ExoPlayerDemo/"
            + ExoPlayerLibraryInfo.VERSION
            + " (Linux; Android "
            + Build.VERSION.RELEASE
            + ") "
            + ExoPlayerLibraryInfo.VERSION_SLASHY)

    private val mHttpDataSourceFactory =  DefaultHttpDataSource.Factory().setUserAgent(userAgent)

    var downloadManager = DownloadManager(
        mContext,
        mDatabaseProvider,
        mDownloadCache,
        mHttpDataSourceFactory,
        Executors.newFixedThreadPool(6))
}