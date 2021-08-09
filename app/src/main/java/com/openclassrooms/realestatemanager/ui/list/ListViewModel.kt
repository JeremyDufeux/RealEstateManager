package com.openclassrooms.realestatemanager.ui.list

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyUiListView
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyUiMapView
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import com.openclassrooms.realestatemanager.services.LocationService
import com.openclassrooms.realestatemanager.models.ui.PropertyUiListView
import com.openclassrooms.realestatemanager.models.ui.PropertyUiMapView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val mPropertyUseCase: PropertyUseCase,
    private val mLocationService: LocationService,
    ) : ViewModel(){

    private var _stateLiveData : MutableLiveData<State> = MutableLiveData()
    val stateLiveData: LiveData<State> = _stateLiveData

    private val _propertiesUiListViewLiveData: MutableLiveData<List<PropertyUiListView>> = MutableLiveData()
    val propertiesUiListViewLiveData: LiveData<List<PropertyUiListView>> = _propertiesUiListViewLiveData
    
    private val _propertiesUiMapViewLiveData: MutableLiveData<List<PropertyUiMapView>> = MutableLiveData()
    val propertiesUiMapViewLiveData: LiveData<List<PropertyUiMapView>> = _propertiesUiMapViewLiveData

    private val _location: MutableLiveData<Location> = MutableLiveData()
    val location: LiveData<Location> = _location

    val locationStarted = mLocationService.locationStarted

    init {
        fetchProperties()

        viewModelScope.launch(Dispatchers.IO) {
            mPropertyUseCase.stateFlow.collect { state ->
                if (state is State.Download.DownloadSuccess){
                    _propertiesUiListViewLiveData.postValue(propertyToPropertyUiListView(state.propertiesList))
                    _propertiesUiMapViewLiveData.postValue(propertyToPropertyUiMapView(state.propertiesList))
                }
                _stateLiveData.postValue(state)
            }
        }
    }

    fun fetchProperties(){
        viewModelScope.launch(Dispatchers.IO) {
            mPropertyUseCase.fetchProperties()
        }
    }

    fun startLocationUpdates(){
        viewModelScope.launch(Dispatchers.IO) {
            mLocationService.startLocationUpdates()
            mLocationService.locationFlow.collect { location ->
                _location.postValue(location)
            }
        }
    }

    fun stopLocationUpdates() {
        mLocationService.stopLocationUpdates()
    }
}