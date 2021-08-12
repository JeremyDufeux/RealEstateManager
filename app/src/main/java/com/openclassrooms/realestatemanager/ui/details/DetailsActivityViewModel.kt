package com.openclassrooms.realestatemanager.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsActivityViewModel @Inject constructor(
    private val mPropertyUseCase: PropertyUseCase
) : ViewModel(){

    private var _stateLiveData : MutableLiveData<State> = MutableLiveData()
    val stateLiveData: LiveData<State> = _stateLiveData

    init{
        viewModelScope.launch(Dispatchers.IO) {
            mPropertyUseCase.stateFlow.collect { state ->
                _stateLiveData.postValue(state)
            }
        }
    }

    fun resetState(){
        mPropertyUseCase.resetState()
    }
}