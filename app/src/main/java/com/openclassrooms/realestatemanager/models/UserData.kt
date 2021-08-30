package com.openclassrooms.realestatemanager.models

import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.enums.Unit

data class UserData(
    var unit: Unit = Unit.IMPERIAL,
    var currency: Currency = Currency.DOLLAR
)
