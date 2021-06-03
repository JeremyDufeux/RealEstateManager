package com.openclassrooms.realestatemanager.ui.listFragment

import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.repositories.EstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListFragmentViewModel @Inject constructor(estateRepository : EstateRepository) : ViewModel(){

}