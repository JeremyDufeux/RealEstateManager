package com.openclassrooms.realestatemanager.models.enums

enum class Currency(val currencyName : String, val symbol : String) {
    DOLLAR("Dollar","$"),
    EURO("Euro", "€");

    override fun toString(): String {
        return currencyName
    }
}