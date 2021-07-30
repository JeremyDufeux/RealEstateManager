package com.openclassrooms.realestatemanager.models.enums

enum class OrientationMode(val rotation: Int) {
    ORIENTATION_PORTRAIT_NORMAL(90),
    ORIENTATION_PORTRAIT_INVERTED(270),
    ORIENTATION_LANDSCAPE_NORMAL(0),
    ORIENTATION_LANDSCAPE_INVERTED(180)
}