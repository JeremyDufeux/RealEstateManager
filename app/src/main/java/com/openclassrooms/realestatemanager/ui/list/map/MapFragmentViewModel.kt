package com.openclassrooms.realestatemanager.ui.list.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.State
import com.openclassrooms.realestatemanager.repositories.PropertyRepository
import com.openclassrooms.realestatemanager.services.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapFragmentViewModel @Inject constructor(
    private val mPropertyRepository: PropertyRepository,
    private val mLocationService: LocationService,
    ) : ViewModel(){

    private var mPropertyListMutableLiveData : MutableLiveData<State<List<Property>>> = MutableLiveData()
    val propertyListLiveData: LiveData<State<List<Property>>> = mPropertyListMutableLiveData

    private val _location: MutableLiveData<Location> = MutableLiveData()
    val location: LiveData<Location> = _location

    val locationStarted = mLocationService.locationStarted

    init {
        viewModelScope.launch {
            mPropertyRepository.fetchProperties()
            mPropertyRepository.propertiesFlow.collect { list ->
                mPropertyListMutableLiveData.postValue(list)
            }
        }
    }

    fun startLocationUpdates(){
        viewModelScope.launch {
            mLocationService.startLocationUpdates()
            mLocationService.locationFlow.collect(){ location ->
                _location.postValue(location)
            }
        }
    }

    fun stopLocationUpdates() {
        mLocationService.stopLocationUpdates()
    }
}