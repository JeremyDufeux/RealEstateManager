package com.openclassrooms.realestatemanager.repositories

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.State
import com.openclassrooms.realestatemanager.services.PropertyApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor(
    private val mPropertyApiService: PropertyApiService){

    private var mProperties: MutableList<Property> = mutableListOf()

    private val _propertiesFlow = MutableStateFlow<State>(State.Idle)
    val propertiesFlow = _propertiesFlow.asStateFlow()

    suspend fun fetchProperties() {
        mPropertyApiService.fetchProperties()
            .collect { result ->
                if(result is State.Download.DownloadSuccess){
                    mProperties = result.propertiesList as MutableList<Property>
                }
                _propertiesFlow.value = result
        }
    }

    fun getPropertyWithId(propertyId: String): Property {
        val estateToUpdate = mProperties.find {it.id == propertyId }
        val estateIndex = mProperties.indexOf(estateToUpdate)
        return mProperties[estateIndex]
    }

    suspend fun addPropertyAndFetch(property: Property) {
        _propertiesFlow.value = State.Upload.Uploading
        uploadMedias(property)
        addProperty(property)
        _propertiesFlow.value = State.Download.Downloading
        fetchProperties()
    }

    private suspend fun uploadMedias(property: Property) {
        for(medias in property.mediaList){
            when(val state = mPropertyApiService.uploadMedia(medias.url)){
                is State.Upload.UploadSuccess -> medias.url = state.url
                is State.Upload.Error -> _propertiesFlow.value = state
                else -> {}
            }
        }
    }

    private fun addProperty(property: Property) {
        mPropertyApiService.addProperty(property)
    }
}