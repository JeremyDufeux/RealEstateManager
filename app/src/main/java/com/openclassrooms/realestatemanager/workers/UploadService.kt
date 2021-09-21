package com.openclassrooms.realestatemanager.workers

interface UploadService {
    fun enqueueUploadWorker()
    fun cancelUploadWorker()
}