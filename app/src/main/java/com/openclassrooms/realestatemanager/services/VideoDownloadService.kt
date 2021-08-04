package com.openclassrooms.realestatemanager.services

import android.app.Notification
import android.content.Context
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.util.Util
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val JOB_ID = 1

@AndroidEntryPoint
class VideoDownloadService: DownloadService(
    FOREGROUND_NOTIFICATION_ID_NONE,
    0,
    null,
    0,
    0) {

    @Inject
    @ApplicationContext
    lateinit var mContext: Context

    override fun getDownloadManager(): DownloadManager {
        return (application as RealEstateManagerApplication).appContainer.downloadManager.apply {
            maxParallelDownloads = 5
        }
    }

    override fun getScheduler(): Scheduler? {
        return if (Util.SDK_INT >= 21) PlatformScheduler(this, JOB_ID) else null
    }

    override fun getForegroundNotification(downloads: MutableList<Download>): Notification {
        throw UnsupportedOperationException()
    }
}