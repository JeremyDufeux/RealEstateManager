package com.openclassrooms.realestatemanager.ui.listFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.repositories.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListFragmentViewModel @Inject constructor(private val propertyRepository : PropertyRepository) : ViewModel(){

    private val mPropertyListLiveData : MutableLiveData<List<Property>> = MutableLiveData()

    init {
        viewModelScope.launch {
            propertyRepository.propertyList.collect { propertyList ->
                mPropertyListLiveData.postValue(propertyList)
            }
        }

    }

    fun observePropertyList() : LiveData<List<Property>>{
        return mPropertyListLiveData
    }
}