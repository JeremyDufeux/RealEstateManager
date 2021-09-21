package com.openclassrooms.realestatemanager.worker

import com.openclassrooms.realestatemanager.workers.UploadService
import javax.inject.Singleton

@Singleton
class FakeUploadService: UploadService{
    override fun enqueueUploadWorker() {}

    override fun cancelUploadWorker() {}
}