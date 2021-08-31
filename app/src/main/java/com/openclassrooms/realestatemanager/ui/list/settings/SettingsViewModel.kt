package com.openclassrooms.realestatemanager.ui.list.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.models.UserData
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.enums.Unit
import com.openclassrooms.realestatemanager.modules.IoCoroutineScope
import com.openclassrooms.realestatemanager.repositories.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @IoCoroutineScope private val mIoScope: CoroutineScope,
    private val mUserDataRepository: UserDataRepository)
    : ViewModel() {

    private var _userDataLiveData : MutableLiveData<UserData> = MutableLiveData()
    var userDataLiveData: LiveData<UserData> = _userDataLiveData

    var userData = UserData(Unit.IMPERIAL, Currency.DOLLAR)

    init {
        mIoScope.launch {
            mUserDataRepository.userDataFlow.collect {
                userData = it
                _userDataLiveData.postValue(it)
            }
        }
    }

    fun saveSettings(){
        mUserDataRepository.saveUserData(userData.unit, userData.currency)
    }

}