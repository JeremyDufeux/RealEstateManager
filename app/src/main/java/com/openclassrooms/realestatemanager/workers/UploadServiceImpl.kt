package com.openclassrooms.realestatemanager.workers

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

const val UPLOAD_WORKER_NAME = "UPLOAD_WORKER_NAME"

@Singleton
class UploadServiceImpl @Inject constructor(
    @ApplicationContext private val mContext: Context): UploadService{

    override fun enqueueUploadWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWork = PeriodicWorkRequestBuilder<UploadWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(mContext).enqueueUniquePeriodicWork(UPLOAD_WORKER_NAME, ExistingPeriodicWorkPolicy.REPLACE, uploadWork)
    }

    override fun cancelUploadWorker(){
        WorkManager.getInstance(mContext).cancelUniqueWork(UPLOAD_WORKER_NAME)
    }
}