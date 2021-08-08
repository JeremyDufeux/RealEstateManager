package com.openclassrooms.realestatemanager.models.sealedClasses

import com.openclassrooms.realestatemanager.models.Property

sealed class State {
    object Idle : State()
    sealed class Download {
        object Downloading : State()
        data class DownloadSuccess(val propertiesList: List<Property>): State()
        data class Error(val throwable: Throwable?): State()
    }
    sealed class Upload {
        object Uploading : State()
        sealed class UploadSuccess {
            object Success: State()
            data class SuccessWithUrl(val url: String) : State()
        }
        data class Error(val throwable: Throwable?): State()
    }
}