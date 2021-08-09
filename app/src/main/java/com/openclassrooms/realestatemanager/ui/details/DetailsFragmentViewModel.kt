package com.openclassrooms.realestatemanager.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyUiDetailsView
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import com.openclassrooms.realestatemanager.models.ui.PropertyUiDetailsView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsFragmentViewModel @Inject constructor(
    private val mPropertyUseCase: PropertyUseCase
    ) : ViewModel(){

    private var mPropertyMutableLiveData : MutableLiveData<PropertyUiDetailsView> = MutableLiveData()
    var propertyLiveData: LiveData<PropertyUiDetailsView> = mPropertyMutableLiveData

    fun setPropertyId(propertyId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mPropertyUseCase.getPropertyWithIdFlow(propertyId).collect { property ->
                mPropertyMutableLiveData.postValue(propertyToPropertyUiDetailsView(property))
            }
        }
    }
}