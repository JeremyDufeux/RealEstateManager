package com.openclassrooms.realestatemanager.ui.list.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.State
import com.openclassrooms.realestatemanager.repositories.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListFragmentViewModel @Inject constructor(
    private val mPropertyRepository : PropertyRepository
    ) : ViewModel() {

    private var mPropertyListMutableLiveData : MutableLiveData<State<List<Property>>> = MutableLiveData()
    val propertyListLiveData: LiveData<State<List<Property>>> = mPropertyListMutableLiveData

    init {
        viewModelScope.launch {
            mPropertyRepository.fetchProperties()
            mPropertyRepository.propertiesFlow.collect { list ->
                mPropertyListMutableLiveData.postValue(list)
            }
        }
    }
}