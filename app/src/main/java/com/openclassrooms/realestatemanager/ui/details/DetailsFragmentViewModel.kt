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
            val property = mPropertyUseCase.getPropertyWithId(propertyId)
            updatePropertyFields(property)
            mPropertyMutableLiveData.postValue(property)
        }
    }

    private fun updatePropertyFields(property: Property) {
        property.apply {
            priceVisibility = getPriceVisibility(this)
            surfaceVisibility = getSurfaceVisibility(this)
            descriptionVisibility = getDescriptionVisibility(this)
            roomsVisibility = getRoomVisibility(this)
            bathroomsVisibility = getBathroomVisibility(this)
            bedroomsVisibility = getBedroomVisibility(this)
            formattedAddress = getFormattedAddress(this)
            addressVisibility = getAddressVisibility(this)
            pointsOfInterestVisibility = getPointsOfInterestVisibility(this)
            mapVisibility = getMapVisibility(this)
            agentVisibility = getAgentVisibility(this)
        }
    }
}