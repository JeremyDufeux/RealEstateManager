package com.openclassrooms.realestatemanager.models

import java.io.File

sealed class FileState{
    data class Success(val file: File) : FileState()
    data class Error(val StringId: Int) : FileState()
    object Empty : FileState()
}