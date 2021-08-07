package com.openclassrooms.realestatemanager.repositories

import android.content.Context
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.enums.ServerState
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.modules.IoCoroutineScope
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.throwable.OfflineError
import com.openclassrooms.realestatemanager.workers.UploadService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyUseCase @Inject constructor(
    @ApplicationContext private val mContext: Context,
    @IoCoroutineScope private val mIoScope: CoroutineScope,
    private val mPropertyRepository: PropertyRepository,
    private val mOfflinePropertyRepository: OfflinePropertyRepository,
    private val mUploadService: UploadService) {

    private val _stateFlow = MutableStateFlow<State>(State.Idle)
    val stateFlow = _stateFlow.asStateFlow()

    init {
        mIoScope.launch {
            mPropertyRepository.stateFlow.collect { result ->
                _stateFlow.value = result
                if(result is State.Download.DownloadSuccess){
                    mOfflinePropertyRepository.updateDatabase(result.propertiesList)
                }
            }
        }
    }

    suspend fun fetchProperties(){
        if (Utils.isInternetAvailable(mContext)) {
            mPropertyRepository.fetchProperties()
        } else {
            _stateFlow.value = State.Download.Error(OfflineError())
            getOfflineProperties()
        }
    }

    private fun getOfflineProperties() {
        mOfflinePropertyRepository.getProperties().let {
            _stateFlow.value = State.Download.DownloadSuccess(it)
        }
    }

    fun getPropertyWithIdFlow(propertyId: String): Flow<Property> {
        return mOfflinePropertyRepository.getPropertyWithIdFlow(propertyId)
    }

    suspend fun addProperty(property: Property) {
        if (Utils.isInternetAvailable(mContext)) {
            mPropertyRepository.addPropertyWithMedias(property)
            mPropertyRepository.fetchProperties()
        }
        else {
            _stateFlow.value = State.Upload.Error(OfflineError())
            mOfflinePropertyRepository.addProperty(property, ServerState.WAITING_UPLOAD)
            mUploadService.enqueueUploadWorker()
            getOfflineProperties()
            _stateFlow.value = State.Idle
        }
    }

    suspend fun updateProperty(newProperty: Property) {
        val oldProperty = mOfflinePropertyRepository.getPropertyWithId(newProperty.id)

        if (Utils.isInternetAvailable(mContext)) {
            updatePropertyOnline(oldProperty, newProperty)
            mPropertyRepository.fetchProperties()
        }
        else {
            _stateFlow.value = State.Upload.Error(OfflineError())
            updatePropertyOffline(oldProperty, newProperty)
            mUploadService.enqueueUploadWorker()
            getOfflineProperties()
            _stateFlow.value = State.Idle
        }
    }

    private suspend fun updatePropertyOnline(oldProperty: Property, newProperty: Property) {
        _stateFlow.value = State.Upload.Uploading

        for(mediaItem in newProperty.mediaList){
            if(oldProperty.mediaList.firstOrNull{ it.id == mediaItem.id} == null){ // Media as been added
                mPropertyRepository.uploadMedia(mediaItem)
            }
        }
        for(mediaItem in oldProperty.mediaList){
            if(newProperty.mediaList.firstOrNull{ it.id == mediaItem.id} == null){ // Media as been deleted
                mPropertyRepository.deleteMedia(mediaItem)
                mOfflinePropertyRepository.deleteMedia(mediaItem)
            }
        }
        mPropertyRepository.addProperty(newProperty)
    }

    private suspend fun updatePropertyOffline(oldProperty: Property, newProperty: Property) {
        for(mediaItem in newProperty.mediaList){
            if(oldProperty.mediaList.firstOrNull{ it.id == mediaItem.id} == null){ // Media as been added
                mOfflinePropertyRepository.addMediaToUpload(mediaItem)
            }
        }
        for(mediaItem in oldProperty.mediaList){
            if(newProperty.mediaList.firstOrNull{ it.id == mediaItem.id} == null){ // Media as been deleted
                mOfflinePropertyRepository.setMediaToDelete(mediaItem)
            }
        }
        mOfflinePropertyRepository.updateProperty(newProperty)
    }

    suspend fun uploadPendingProperties(){
        _stateFlow.value = State.Upload.Uploading

        for (mediaItem in mOfflinePropertyRepository.getMediaItemsToUpload()) {
            mPropertyRepository.uploadMedia(mediaItem)
        }

        for (mediaItem in mOfflinePropertyRepository.getMediaItemsToDelete()) {
            mPropertyRepository.deleteMedia(mediaItem)
            mOfflinePropertyRepository.deleteMedia(mediaItem)
        }

        for (property in mOfflinePropertyRepository.getPropertiesToUpload()) {
            mPropertyRepository.addProperty(property)
        }

        mPropertyRepository.fetchProperties()
    }
}