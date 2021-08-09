package com.openclassrooms.realestatemanager.repositories

import android.content.Context
import android.content.SharedPreferences
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.enums.Unit
import com.openclassrooms.realestatemanager.utils.find
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

const val FILE_NAME = "UserData"
const val PREF_KEY_UNIT = "PREF_KEY_UNIT"
const val PREF_KEY_CURRENCY = "PREF_KEY_CURRENCY"

class UserDataRepository @Inject constructor(
    @ApplicationContext private val mContext: Context
) {
    private var mPreferences: SharedPreferences = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    val unitFlow: Flow<Unit> = flow{
        val unitString = mPreferences.getString(PREF_KEY_UNIT, Unit.IMPERIAL.unitName)!!
        val unit = Unit::unitName.find(unitString)!!
        emit(unit)
    }

    val currencyFlow: Flow<Currency> = flow{
        val currencyString = mPreferences.getString(PREF_KEY_CURRENCY, Currency.DOLLAR.currencyName)!!
        val currency = Currency::currencyName.find(currencyString)!!
        emit(currency)
    }

    fun saveUserData(unit: Unit, currency: Currency){
        mPreferences.edit().putString(PREF_KEY_UNIT, unit.unitName).apply()
        mPreferences.edit().putString(PREF_KEY_CURRENCY, currency.currencyName).apply()
    }
}