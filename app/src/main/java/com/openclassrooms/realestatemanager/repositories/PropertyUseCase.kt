package com.openclassrooms.realestatemanager.repositories

import android.content.Context
import androidx.work.ListenableWorker.Result
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.PropertyFilter
import com.openclassrooms.realestatemanager.models.enums.DataState
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.modules.IoCoroutineScope
import com.openclassrooms.realestatemanager.services.GeocoderClient
import com.openclassrooms.realestatemanager.services.LifecycleService
import com.openclassrooms.realestatemanager.services.NotificationService
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.getStaticMapUrl
import com.openclassrooms.realestatemanager.utils.throwable.OfflineError
import com.openclassrooms.realestatemanager.workers.UploadService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
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
    private val mUploadService: UploadService,
    private val mGeocoderClient: GeocoderClient,
    private val mNotificationService: NotificationService,
    private val mLifecycleService: LifecycleService
){

    private val _stateFlow = MutableStateFlow<State>(State.Idle)
    val stateFlow = _stateFlow.asStateFlow()

    init {
        mIoScope.launch {
            mPropertyRepository.stateFlow.collect { result ->
                _stateFlow.value = result
                if(result is State.Download.DownloadSuccess){
                    mOfflinePropertyRepository.updateDatabase(result.propertiesList)
                }
                else if(result is State.Upload.UploadSuccess.Empty){
                    onUploadSuccess()
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

    fun getOfflineProperties() {
        mOfflinePropertyRepository.getProperties().let {
            _stateFlow.value = State.OfflineSuccess(it)
        }
    }

    fun getPropertyWithIdFlow(propertyId: String): Flow<Property> {
        return mOfflinePropertyRepository.getPropertyWithIdFlow(propertyId)
    }

    suspend fun addProperty(property: Property) {
        mOfflinePropertyRepository.addProperty(property, DataState.WAITING_UPLOAD)

        if (Utils.isInternetAvailable(mContext)) {
            mPropertyRepository.addPropertyWithMedias(property)
        }
        else {
            getOfflineProperties()
            mUploadService.enqueueUploadWorker()
            _stateFlow.value = State.Upload.Error(OfflineError())
        }
    }

    suspend fun updateProperty(newProperty: Property) {
        val oldProperty = mOfflinePropertyRepository.getPropertyWithId(newProperty.id)
        if (oldProperty != null) {
            updatePropertyOffline(oldProperty, newProperty)

            if (Utils.isInternetAvailable(mContext)) {
                updatePropertyOnline(oldProperty, newProperty)
            } else {
                getOfflineProperties()
                mUploadService.enqueueUploadWorker()
                _stateFlow.value = State.Upload.Error(OfflineError())
            }
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

    suspend fun uploadPendingProperties(): Result {
        delay(10000) // Needed to initialize GeocoderClient properly

        _stateFlow.value = State.Upload.Uploading

        for (mediaItem in mOfflinePropertyRepository.getMediaItemsToUpload()) {
            mPropertyRepository.uploadMedia(mediaItem)
        }

        for (mediaItem in mOfflinePropertyRepository.getMediaItemsToDelete()) {
            mPropertyRepository.deleteMedia(mediaItem)
            mOfflinePropertyRepository.deleteMedia(mediaItem)
        }

        for (property in mOfflinePropertyRepository.getPropertiesToUpload()) {
            updatePropertyLocation(property)
            mPropertyRepository.addProperty(property)
        }

        _stateFlow.value = State.Upload.UploadSuccess.Empty

        return Result.success()
    }

    private suspend fun onUploadSuccess(){
        if(!mLifecycleService.isAppForeground()) {
            mNotificationService.createNotification()
        }
        mUploadService.cancelUploadWorker()
        mPropertyRepository.fetchProperties()
    }

    private fun updatePropertyLocation(property: Property){
        val latLng =
            mGeocoderClient.getPropertyLocation(
                property.addressLine1,
                property.addressLine2,
                property.city,
                property.postalCode,
                property.country
            )

        if (latLng != null) {
            property.latitude = latLng.latitude
            property.longitude = latLng.longitude
            property.mapPictureUrl = getStaticMapUrl(property.latitude, property.longitude)
        }
    }

    fun resetState(){
        _stateFlow.value = State.Idle
    }

    fun getPropertyWithFilters(propertyFilter: PropertyFilter) {
        mOfflinePropertyRepository.getPropertyWithFilters(propertyFilter).let {
            _stateFlow.value = State.Filter.Result(it)
        }
    }

    fun removeFilters() {
        _stateFlow.value = State.Filter.Clear
        mOfflinePropertyRepository.getProperties().let {
            _stateFlow.value = State.Download.DownloadSuccess(it)
        }
    }
}