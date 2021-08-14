package com.openclassrooms.realestatemanager.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class DetailsActivityViewModel @Inject constructor(
    private val mPropertyUseCase: PropertyUseCase
) : ViewModel(){

    val stateLiveData: LiveData<State> = mPropertyUseCase.stateFlow.asLiveData(Dispatchers.IO)

    fun resetState(){
        mPropertyUseCase.resetState()
    }
}