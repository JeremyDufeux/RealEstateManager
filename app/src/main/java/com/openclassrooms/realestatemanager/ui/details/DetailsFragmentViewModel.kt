package com.openclassrooms.realestatemanager.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.repositories.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsFragmentViewModel @Inject constructor(
    private val mPropertyRepository: PropertyRepository
    ) : ViewModel(){

    private val mPropertyMutableLiveData : MutableLiveData<Property> = MutableLiveData()
    val propertyLiveData: LiveData<Property> = mPropertyMutableLiveData

    fun setPropertyId(propertyId: String) {
        viewModelScope.launch {
            mPropertyMutableLiveData.postValue(mPropertyRepository.getPropertyWithId(propertyId))
        }
    }
}