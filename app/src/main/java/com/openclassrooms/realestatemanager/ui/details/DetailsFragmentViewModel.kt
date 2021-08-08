package com.openclassrooms.realestatemanager.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.mappers.*
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsFragmentViewModel @Inject constructor(
    private val mPropertyUseCase: PropertyUseCase
    ) : ViewModel(){

    private var mPropertyMutableLiveData : MutableLiveData<Property> = MutableLiveData()
    var propertyLiveData: LiveData<Property> = mPropertyMutableLiveData

    fun setPropertyId(propertyId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mPropertyUseCase.getPropertyWithIdFlow(propertyId).collect { property ->
                updatePropertyFields(property)
                mPropertyMutableLiveData.postValue(property)
            }
        }
    }

    private fun updatePropertyFields(property: Property) {
        updatePriceVisibility(property)
        updateSurfaceVisibility(property)
        updateDescriptionVisibility(property)
        updateRoomVisibility(property)
        updateBathroomVisibility(property)
        updateBedroomVisibility(property)
        updateFormattedAddress(property)
        updateAddressVisibility(property)
        updatePointsOfInterestVisibility(property)
        updateMapVisibility(property)
        updateSoldDateVisibility(property)
        updateAgentVisibility(property)
    }
}