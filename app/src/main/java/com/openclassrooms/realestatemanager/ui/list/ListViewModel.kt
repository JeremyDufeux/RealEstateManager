package com.openclassrooms.realestatemanager.ui.list

import android.location.Location
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.mappers.PropertyToPropertyUiListViewMapper
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyUiMapView
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.PropertyFilter
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.enums.Unit
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.models.ui.PropertyUiListView
import com.openclassrooms.realestatemanager.models.ui.PropertyUiMapView
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import com.openclassrooms.realestatemanager.repositories.UserDataRepository
import com.openclassrooms.realestatemanager.services.LocationService
import com.openclassrooms.realestatemanager.utils.Utils.convertEurosToDollars
import com.openclassrooms.realestatemanager.utils.Utils.convertSquareMetersToSquareFoot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val mPropertyUseCase: PropertyUseCase,
    private val mLocationService: LocationService,
    private val mListViewMapper : PropertyToPropertyUiListViewMapper,
    mUserDataRepository: UserDataRepository
    ) : ViewModel(){

    val stateLiveData: LiveData<State> = mPropertyUseCase.stateFlow.asLiveData(Dispatchers.IO)

    private val propertyListSuccessFlow: Flow<List<Property>> = mPropertyUseCase.stateFlow
        .filterIsInstance<State.Download.DownloadSuccess>()
        .map { it.propertiesList }

    private val propertyListFilterFlow: Flow<List<Property>> = mPropertyUseCase.stateFlow
        .filterIsInstance<State.Filter.Result>()
        .map { it.propertiesList }

    private val propertyMergedListFlow: Flow<List<Property>> = merge(propertyListSuccessFlow, propertyListFilterFlow)

    val propertiesUiListViewLiveData: LiveData<List<PropertyUiListView>> =
        combine(propertyMergedListFlow, mUserDataRepository.userDataFlow){ propertiesList, userData ->
            mListViewMapper.map(propertiesList, userData.currency)
        }.asLiveData(Dispatchers.IO)

    val propertiesUiMapViewLiveData: LiveData<List<PropertyUiMapView>> = propertyMergedListFlow
        .map { propertyToPropertyUiMapView(it) }
        .asLiveData(Dispatchers.IO)

    private var _selectedPropertyLiveData : MutableLiveData<String?> = MutableLiveData()
    val selectedPropertyLiveData: LiveData<String?> = _selectedPropertyLiveData

    val location: LiveData<Location> = mLocationService.locationFlow.asLiveData(Dispatchers.IO)

    val locationStarted = mLocationService.locationStarted

    var selectedPropertyIdForTabletLan: String? = null

    var propertyFilter = PropertyFilter()

    init {
        fetchProperties()

        viewModelScope.launch(Dispatchers.IO) {
            mUserDataRepository.userDataFlow.collect {
                propertyFilter.userData = it
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
            if(!propertyFilter.isDefaultValues()) {
                val filter = propertyFilter.copy()
                filter.userData = propertyFilter.userData.copy()
                filter.minPrice = propertyFilter.minPrice
                filter.maxPrice = propertyFilter.maxPrice
                filter.minSurface = propertyFilter.minSurface
                filter.maxSurface = propertyFilter.maxSurface

                if(filter.userData.currency == Currency.EURO) {
                    if(filter.minPrice != 0L) {
                        filter.minPrice =
                            convertEurosToDollars(filter.minPrice.toDouble()).toLong()
                    }
                    if(filter.maxPrice != 0L) {
                        filter.maxPrice =
                            convertEurosToDollars(filter.maxPrice.toDouble()).toLong()
                    }
                }

                if(filter.userData.unit == Unit.METRIC) {
                    filter.minSurface = convertSquareMetersToSquareFoot(filter.minSurface.toDouble()).toLong()
                    filter.maxSurface = convertSquareMetersToSquareFoot(filter.maxSurface.toDouble()).toLong()
                }
                mPropertyUseCase.getPropertyWithFilters(filter)
            }
        }
    }

    fun removeFilters() {
        propertyFilter.clearFilters()
        viewModelScope.launch(Dispatchers.IO) {
            mPropertyUseCase.removeFilters()
        }
    }

    fun resetFilters() {
        propertyFilter.clearFilters()
    }
}