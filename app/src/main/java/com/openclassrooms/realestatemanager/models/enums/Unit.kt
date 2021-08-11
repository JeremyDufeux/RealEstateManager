package com.openclassrooms.realestatemanager.models.enums

enum class Unit(val unitName: String, val abbreviation: String) {
    IMPERIAL("Imperial", "sq ft"),
    METRIC("Metric","m²");

    override fun toString(): String {
        return unitName
    }
}