package com.openclassrooms.realestatemanager.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.mappers.propertyToPropertyUiDetailsView
import com.openclassrooms.realestatemanager.models.ui.PropertyUiDetailsView
import com.openclassrooms.realestatemanager.repositories.PropertyUseCase
import com.openclassrooms.realestatemanager.repositories.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsFragmentViewModel @Inject constructor(
    private val mPropertyUseCase: PropertyUseCase,
    private val mUserDataRepository: UserDataRepository
    ) : ViewModel(){

    private var mPropertyMutableLiveData : MutableLiveData<PropertyUiDetailsView> = MutableLiveData()
    var propertyLiveData: LiveData<PropertyUiDetailsView> = mPropertyMutableLiveData

    private lateinit var job: Job

    fun setPropertyId(propertyId: String) {
        if(this::job.isInitialized){
            job.cancel()
        }

        job = viewModelScope.launch(Dispatchers.IO) {
            combine(mPropertyUseCase.getPropertyWithIdFlow(propertyId), mUserDataRepository.userDataFlow){ property, userData ->
                propertyToPropertyUiDetailsView(property, userData)
            }.collect { property ->
                mPropertyMutableLiveData.postValue(property)
            }
        }
    }
}