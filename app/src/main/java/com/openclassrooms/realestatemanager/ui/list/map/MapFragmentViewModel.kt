package com.openclassrooms.realestatemanager.ui.list.map

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
class MapFragmentViewModel @Inject constructor(
    private val mPropertyRepository: PropertyRepository
    ) : ViewModel(){

    private val mPropertyListMutableLiveData : MutableLiveData<List<Property>> = MutableLiveData()
    val propertyListLiveData: LiveData<List<Property>> = mPropertyListMutableLiveData

    init {
        viewModelScope.launch {
                mPropertyListMutableLiveData.postValue(mPropertyRepository.getPropertyList())
        }
    }
}