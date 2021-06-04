package com.openclassrooms.realestatemanager.ui.listFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.repositories.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListFragmentViewModel @Inject constructor(propertyRepository : PropertyRepository) : ViewModel(){

    private val mPropertyListLiveData : MutableLiveData<List<Property>> = MutableLiveData()

    init {
        mPropertyListLiveData.postValue(propertyRepository.getPropertyList())
    }

    fun observePropertyList() : LiveData<List<Property>>{
        return mPropertyListLiveData
    }
}