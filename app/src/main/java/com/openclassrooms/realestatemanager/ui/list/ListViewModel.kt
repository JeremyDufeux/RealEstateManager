package com.openclassrooms.realestatemanager.ui.list

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.mappers.updatePriceVisibility
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import com.openclassrooms.realestatemanager.services.LocationService
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

    private val _location: MutableLiveData<Location> = MutableLiveData()
    val location: LiveData<Location> = _location

    val locationStarted = mLocationService.locationStarted

    init {
        fetchProperties()

        viewModelScope.launch(Dispatchers.IO) {
            mPropertyUseCase.stateFlow.collect { state ->
                if (state is State.Download.DownloadSuccess){
                    updatePropertyFields(state.propertiesList)
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

    private fun updatePropertyFields(properties: List<Property>) {
        for (property in properties){
            updatePriceVisibility(property)
        }
    }
}