package com.openclassrooms.realestatemanager.models.enums

import com.openclassrooms.realestatemanager.R

enum class Currency(val currencyNameResId : Int, val symbolResId : Int) {
    DOLLAR(R.string.dollar,R.string.dollar_symbol),
    EURO(R.string.euro, R.string.euro_symbol);

}