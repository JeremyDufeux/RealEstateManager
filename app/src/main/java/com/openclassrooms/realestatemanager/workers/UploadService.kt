package com.openclassrooms.realestatemanager.workers

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val UPLOAD_WORKER_NAME = "UPLOAD_WORKER_TAG"

class UploadService @Inject constructor(
    @ApplicationContext private val mContext: Context) {

    fun enqueueUploadWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWork = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
            .setInitialDelay(20000, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(mContext).enqueueUniqueWork(UPLOAD_WORKER_NAME, ExistingWorkPolicy.REPLACE, uploadWork)
    }
}