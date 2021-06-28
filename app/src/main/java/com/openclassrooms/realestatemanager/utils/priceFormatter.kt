package com.openclassrooms.realestatemanager.utils

import java.text.DecimalFormat

fun priceFormatter(input : String) : String {
    val priceFormatPattern = DecimalFormat("#,###")
    val v = input.replace(priceFormatPattern.decimalFormatSymbols.groupingSeparator.toString(), "")
    val n = priceFormatPattern.parse(v)
    return priceFormatPattern.format(n)
}