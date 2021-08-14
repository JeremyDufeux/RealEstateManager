package com.openclassrooms.realestatemanager.ui.details

import androidx.lifecycle.*
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

const val DETAILS_FRAGMENT_SAVED_STATE_PROPERTY = "DETAILS_FRAGMENT_SAVED_STATE_PROPERTY"

@HiltViewModel
class DetailsFragmentViewModel @Inject constructor(
    private val mSavedStateHandle: SavedStateHandle,
    private val mPropertyUseCase: PropertyUseCase,
    private val mUserDataRepository: UserDataRepository
    ) : ViewModel(){

    private var mPropertyMutableLiveData : MutableLiveData<PropertyUiDetailsView> = MutableLiveData()
    var propertyLiveData: LiveData<PropertyUiDetailsView> = mPropertyMutableLiveData

    private lateinit var job: Job

    init {
        if(mSavedStateHandle.contains(DETAILS_FRAGMENT_SAVED_STATE_PROPERTY)){
            setPropertyId(mSavedStateHandle.get<String>(DETAILS_FRAGMENT_SAVED_STATE_PROPERTY)!!)
        }
    }

    fun setPropertyId(propertyId: String) {
        mSavedStateHandle.set(DETAILS_FRAGMENT_SAVED_STATE_PROPERTY, propertyId)

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