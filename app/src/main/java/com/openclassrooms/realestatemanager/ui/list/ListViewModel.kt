package com.openclassrooms.realestatemanager.ui.list

import android.location.Location
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyUiListView
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyUiMapView
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.models.ui.PropertyUiListView
import com.openclassrooms.realestatemanager.models.ui.PropertyUiMapView
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import com.openclassrooms.realestatemanager.repositories.UserDataRepository
import com.openclassrooms.realestatemanager.services.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val mPropertyUseCase: PropertyUseCase,
    private val mLocationService: LocationService,
    mUserDataRepository: UserDataRepository
    ) : ViewModel(){

    val stateLiveData: LiveData<State> = mPropertyUseCase.stateFlow.asLiveData(Dispatchers.IO)

    val stateFlowSuccess: Flow<State.Download.DownloadSuccess> = mPropertyUseCase.stateFlow.filterIsInstance()

    val propertiesUiListViewLiveData: LiveData<List<PropertyUiListView>> =
        combine(stateFlowSuccess, mUserDataRepository.userDataFlow){ state, userData ->
            propertyToPropertyUiListView(state.propertiesList, userData.currency)
        }.asLiveData(Dispatchers.IO)

    val propertiesUiMapViewLiveData: LiveData<List<PropertyUiMapView>> = stateFlowSuccess
        .map { propertyToPropertyUiMapView(it.propertiesList) }
        .asLiveData(Dispatchers.IO)

    private var _selectedPropertyLiveData : MutableLiveData<String?> = MutableLiveData()
    val selectedPropertyLiveData: LiveData<String?> = _selectedPropertyLiveData

    val location: LiveData<Location> = mLocationService.locationFlow.asLiveData(Dispatchers.IO)

    val locationStarted = mLocationService.locationStarted

    var selectedPropertyIdForTabletLan: String? = null

    init {
        fetchProperties()
    }

    fun fetchProperties(){
        viewModelScope.launch(Dispatchers.IO) {
            mPropertyUseCase.getOfflineProperties()
            mPropertyUseCase.fetchProperties()
        }
    }

    fun startLocationUpdates(){
        mLocationService.startLocationUpdates()
    }

    fun stopLocationUpdates() {
        mLocationService.stopLocationUpdates()
    }

    fun resetState() {
        mPropertyUseCase.resetState()
    }

    fun setSelectedPropertyId(propertyId: String?){
        _selectedPropertyLiveData.value = propertyId
    }
}