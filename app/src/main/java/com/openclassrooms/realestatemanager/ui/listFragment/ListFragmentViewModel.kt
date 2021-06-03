package com.openclassrooms.realestatemanager.ui.listFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.models.Estate
import com.openclassrooms.realestatemanager.repositories.EstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListFragmentViewModel @Inject constructor(estateRepository : EstateRepository) : ViewModel(){

    private val estateListLiveData : MutableLiveData<List<Estate>> = MutableLiveData()

    init {
        estateListLiveData.postValue(estateRepository.getEstateList())
    }

    fun observeEstateList() : LiveData<List<Estate>>{
        return estateListLiveData
    }
}