package com.openclassrooms.realestatemanager.services

import com.openclassrooms.realestatemanager.models.MediaItem

class FakeVideoDownloaderService: VideoDownloadService {
    override fun startDownloads() {
    }

    override fun cacheVideo(mediaItem: MediaItem) {
    }

    override fun deleteVideo(mediaItem: MediaItem) {
    }
}