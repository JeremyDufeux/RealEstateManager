package com.openclassrooms.realestatemanager.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
        viewModelScope.launch {
            mPropertyUseCase.getPropertyWithId(propertyId).collect { property ->
                mPropertyMutableLiveData.postValue(property)
            }
        }
    }
}