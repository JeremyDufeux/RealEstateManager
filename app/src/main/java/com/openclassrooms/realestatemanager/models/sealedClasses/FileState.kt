package com.openclassrooms.realestatemanager.models.sealedClasses

import com.openclassrooms.realestatemanager.models.MediaItem

sealed class FileState{
    data class Success(val mediaItem: MediaItem) : FileState()
    data class Error(val stringId: Int) : FileState()
    object Empty : FileState()
}