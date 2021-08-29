package com.openclassrooms.realestatemanager.ui.list

import android.location.Location
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.mappers.propertiesToFilterMapper
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyUiListView
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyUiMapView
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.PropertyFilter
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.models.ui.PropertyUiListView
import com.openclassrooms.realestatemanager.models.ui.PropertyUiMapView
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import com.openclassrooms.realestatemanager.repositories.UserDataRepository
import com.openclassrooms.realestatemanager.services.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
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
    val stateFlowFilterResult: Flow<State.Filter.Result> = mPropertyUseCase.stateFlow.filterIsInstance()

    private val propertyListSuccessFlow: Flow<List<Property>> = mPropertyUseCase.stateFlow
        .filterIsInstance<State.Download.DownloadSuccess>()
        .map { it.propertiesList }

    private val propertyListFilterFlow: Flow<List<Property>> = mPropertyUseCase.stateFlow
        .filterIsInstance<State.Filter.Result>()
        .map { it.propertiesList }

    val propertiesUiListViewLiveData: LiveData<List<PropertyUiListView>> =
        combine(merge(propertyListSuccessFlow, propertyListFilterFlow), mUserDataRepository.userDataFlow){ propertiesList, userData ->
        propertyToPropertyUiListView(propertiesList, userData.currency)
        }.asLiveData(Dispatchers.IO)

    val filteredPropertiesUiListViewLiveData: LiveData<List<PropertyUiListView>> =
        combine(propertyListFilterFlow, mUserDataRepository.userDataFlow){ propertiesList, userData ->
            propertyToPropertyUiListView(propertiesList, userData.currency)
        }.asLiveData(Dispatchers.IO)

    val propertiesUiMapViewLiveData: LiveData<List<PropertyUiMapView>> = propertyListSuccessFlow
        .map { propertyToPropertyUiMapView(it) }
        .asLiveData(Dispatchers.IO)

    private val propertiesFilterFlow: Flow<PropertyFilter> =
        combine(propertyListSuccessFlow, mUserDataRepository.userDataFlow){ propertiesList, userData ->
            propertiesToFilterMapper(propertiesList, userData)
        }

    private var _selectedPropertyLiveData : MutableLiveData<String?> = MutableLiveData()
    val selectedPropertyLiveData: LiveData<String?> = _selectedPropertyLiveData

    val location: LiveData<Location> = mLocationService.locationFlow.asLiveData(Dispatchers.IO)

    val locationStarted = mLocationService.locationStarted

    var selectedPropertyIdForTabletLan: String? = null

    lateinit var propertyFilter: PropertyFilter

    init {
        fetchProperties()

        viewModelScope.launch(Dispatchers.IO) {
            propertiesFilterFlow.collect {
                propertyFilter = it
            }
        }
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

    fun applyFilter() {
        viewModelScope.launch(Dispatchers.IO) {
            mPropertyUseCase.getPropertyWithFilters(propertyFilter)
            cancel()
        }
    }

    fun removeFilters() {
        propertyFilter.clearFilters()
        viewModelScope.launch(Dispatchers.IO) {
            mPropertyUseCase.removeFilters()
            cancel()
        }
    }

    fun resetFilters() {
        propertyFilter.clearFilters()
    }
}