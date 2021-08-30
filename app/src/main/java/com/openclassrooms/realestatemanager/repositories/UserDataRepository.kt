package com.openclassrooms.realestatemanager.repositories

import android.content.Context
import android.content.SharedPreferences
import com.openclassrooms.realestatemanager.models.UserData
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.enums.Unit
import com.openclassrooms.realestatemanager.utils.find
import com.openclassrooms.realestatemanager.utils.getStringResourceId
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

const val FILE_NAME = "UserData"
const val PREF_KEY_UNIT = "PREF_KEY_UNIT"
const val PREF_KEY_CURRENCY = "PREF_KEY_CURRENCY"

@Singleton
class UserDataRepository @Inject constructor(
    @ApplicationContext private val mContext: Context
) {
    private var mPreferences: SharedPreferences = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    private val _userDataFlow = MutableSharedFlow<UserData>(replay = 1)
    val userDataFlow = _userDataFlow.asSharedFlow()

    init {
        readUserData()
    }

    private fun readUserData() {
        val unitName = mPreferences.getString(PREF_KEY_UNIT, mContext.getString(Unit.IMPERIAL.unitNameResId))!!
        val unitResId = getStringResourceId(mContext, unitName)
        val unit = Unit::unitNameResId.find(unitResId)!!

        val currencyName = mPreferences.getString(PREF_KEY_CURRENCY, mContext.getString(Currency.DOLLAR.currencyNameResId))!!
        val currencyResId = getStringResourceId(mContext, currencyName)
        val currency = Currency::currencyNameResId.find(currencyResId)!!

        _userDataFlow.tryEmit(UserData(unit, currency))
    }

    fun saveUserData(unit: Unit, currency: Currency){
        mPreferences.edit().putString(PREF_KEY_UNIT, mContext.getString(unit.unitNameResId)).apply()
        mPreferences.edit().putString(PREF_KEY_CURRENCY, mContext.getString(currency.currencyNameResId)).apply()

        _userDataFlow.tryEmit(UserData(unit, currency))
    }
}