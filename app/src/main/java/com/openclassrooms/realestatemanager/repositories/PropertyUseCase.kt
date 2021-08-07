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
            mOfflinePropertyRepository.getProperties().let {
                _stateFlow.value = State.Download.DownloadSuccess(it)
            }
        }
    }

    fun getPropertyWithId(propertyId: String): Property = mOfflinePropertyRepository.getPropertyWithId(propertyId)

    suspend fun addProperty(property: Property) {
        if (Utils.isInternetAvailable(mContext)) {
            mPropertyRepository.addPropertyAndFetch(property)
        } else {
            _stateFlow.value = State.Upload.Error(OfflineError())
            mOfflinePropertyRepository.addProperty(property, ServerState.WAITING_UPLOAD)
            mUploadService.enqueueUploadWorker()
            mOfflinePropertyRepository.getProperties().let {
                _stateFlow.value = State.Download.DownloadSuccess(it)
            }
        }
    }

    suspend fun updateProperty(oldProperty: Property, newProperty: Property) {
        mPropertyRepository.updateProperty(oldProperty, newProperty)
    }

    suspend fun uploadPendingProperties(){
        _stateFlow.value = State.Upload.Uploading
        for (media in mOfflinePropertyRepository.getMediaItemsToUpload()) {
            mPropertyRepository.uploadMedia(media)
        }

        for (property in mOfflinePropertyRepository.getPropertiesToUpload()) {
            mPropertyRepository.addProperty(property)
        }

        mPropertyRepository.fetchProperties()
    }
}