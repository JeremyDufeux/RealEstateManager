package com.openclassrooms.realestatemanager.models

sealed class State {
    object Idle : State()
    sealed class Download{
        object Downloading : State()
        data class DownloadSuccess(val propertiesList: List<Property>): State()
        data class Error(val throwable: Throwable?): State()
    }
    sealed class Upload{
        object Uploading : State()
        data class UploadSuccess(val url: String): State()
        data class Error(val throwable: Throwable?): State()
    }
}