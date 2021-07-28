package com.openclassrooms.realestatemanager.models

sealed class FileState{
    data class Success(val mediaItem: MediaItem) : FileState()
    data class Error(val stringId: Int) : FileState()
    object Empty : FileState()
}