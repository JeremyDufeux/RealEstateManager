package com.openclassrooms.realestatemanager.repositories

import android.content.Context
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.modules.IoCoroutineScope
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.throwable.OfflineError
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
    private val mOfflinePropertyRepository: OfflinePropertyRepository) {

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
            mOfflinePropertyRepository.getProperties().collect {
                _stateFlow.value = State.Download.DownloadSuccess(it)
            }
        }
    }

    fun getPropertyWithId(propertyId: String): Flow<Property> {
        return mOfflinePropertyRepository.getPropertyWithId(propertyId)
    }

    suspend fun addProperty(property: Property) {
        mPropertyRepository.addPropertyAndFetch(property)
    }

    suspend fun updateProperty(oldProperty: Property, newProperty: Property) {
        mPropertyRepository.updateProperty(oldProperty, newProperty)
    }
}