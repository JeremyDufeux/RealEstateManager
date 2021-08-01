package com.openclassrooms.realestatemanager.repositories

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.services.PropertyApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor(
    private val mPropertyApiService: PropertyApiService){

    private val _stateFlow = MutableStateFlow<State>(State.Idle)
    val stateFlow = _stateFlow.asStateFlow()

    suspend fun fetchProperties() {
        _stateFlow.value = State.Download.Downloading
        mPropertyApiService.fetchProperties().let { result ->
            _stateFlow.value = result
        }
    }

    private fun addProperty(property: Property) {
        mPropertyApiService.addProperty(property)
    }

    suspend fun addPropertyAndFetch(property: Property) {
        _stateFlow.value = State.Upload.Uploading
        uploadMedias(property)
        addProperty(property)
        _stateFlow.value = State.Download.Downloading
        fetchProperties()
    }

    private suspend fun uploadMedias(property: Property) {
        for(medias in property.mediaList){
            when(val state = mPropertyApiService.uploadMedia(medias.url)){
                is State.Upload.UploadSuccess -> medias.url = state.url
                is State.Upload.Error -> _stateFlow.value = state
                else -> {}
            }
        }
    }
}