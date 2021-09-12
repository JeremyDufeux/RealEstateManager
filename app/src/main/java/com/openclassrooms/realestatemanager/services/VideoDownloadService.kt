package com.openclassrooms.realestatemanager.services

import com.openclassrooms.realestatemanager.models.MediaItem

interface VideoDownloadService{
    fun startDownloads()
    fun cacheVideo(mediaItem: MediaItem)
    fun deleteVideo(mediaItem: MediaItem)
}