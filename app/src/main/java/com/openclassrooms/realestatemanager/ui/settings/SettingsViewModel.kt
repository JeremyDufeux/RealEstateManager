package com.openclassrooms.realestatemanager.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private var _unitLiveData : MutableLiveData<Unit> = MutableLiveData()
    var unitLiveData: LiveData<Unit> = _unitLiveData

    private var _currencyLiveData : MutableLiveData<Currency> = MutableLiveData()
    var currencyLiveData: LiveData<Currency> = _currencyLiveData

    var unit : Unit = Unit.IMPERIAL
    var currency: Currency = Currency.DOLLAR

    init {
        mIoScope.launch {
            mUserDataRepository.unitFlow.collect {
                unit = it
                _unitLiveData.postValue(it)
            }
            mUserDataRepository.currencyFlow.collect {
                currency = it
                _currencyLiveData.postValue(it)
            }
        }
    }

    fun saveSettings(){
        mUserDataRepository.saveUserData(unit, currency)
    }

}