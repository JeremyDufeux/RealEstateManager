package com.openclassrooms.realestatemanager.models

import android.net.Uri

sealed class FileState{
    data class Success(val uri: Uri, val type: FileType) : FileState()
    data class Error(val stringId: Int) : FileState()
    object Empty : FileState()
}

enum class FileType{
    PICTURE,
    VIDEO,
    OTHER
}