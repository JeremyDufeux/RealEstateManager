package com.openclassrooms.realestatemanager.repositories

import com.openclassrooms.realestatemanager.api.PropertyApiService
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.services.VideoDownloadService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor(
    private val mPropertyApiService: PropertyApiService,
    private val mVideoDownloadService: VideoDownloadService){

    private val _stateFlow = MutableStateFlow<State>(State.Idle)
    val stateFlow = _stateFlow.asStateFlow()

    suspend fun fetchProperties() {
        _stateFlow.value = State.Download.Downloading

        mPropertyApiService.fetchProperties().let { result ->
            _stateFlow.value = result
        }
    }

    fun addProperty(property: Property) {
        mPropertyApiService.addProperty(property)
    }

    suspend fun addPropertyWithMedias(property: Property) {
        _stateFlow.value = State.Upload.Uploading

        for(mediaItem in property.mediaList) {
            uploadMedia(mediaItem)
        }
        addProperty(property)
    }

    suspend fun uploadMedia(mediaItem: MediaItem) {
        when (val state = mPropertyApiService.uploadMedia(mediaItem)) {
            is State.Upload.UploadSuccess -> {
                mediaItem.url = state.url
                _stateFlow.value = State.Idle
            }
            is State.Upload.Error -> _stateFlow.value = state
            else -> {
            }
        }
    }

    suspend fun deleteMedia(mediaItem: MediaItem) {
        val state = mPropertyApiService.deleteMedia(mediaItem)
        if(state is State.Upload.Error){
            _stateFlow.value = state
        }

        if(mediaItem.fileType == FileType.VIDEO){
            mVideoDownloadService.deleteVideo(mediaItem)
        }
    }
}