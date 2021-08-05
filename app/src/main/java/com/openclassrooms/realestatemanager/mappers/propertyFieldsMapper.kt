package com.openclassrooms.realestatemanager.mappers

import android.view.View
import com.openclassrooms.realestatemanager.models.Property


fun getPriceVisibility(property: Property): Int {
    return if (property.price == 0L) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

fun getSurfaceVisibility(property: Property): Int {
    return if (property.surface == 0) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

fun getDescriptionVisibility(property: Property): Int {
    return if (property.description.isBlank()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

fun getRoomVisibility(property: Property): Int {
    return if (property.roomsAmount == 0) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

fun getBathroomVisibility(property: Property): Int {
    return if (property.bathroomsAmount == 0) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

fun getBedroomVisibility(property: Property): Int {
    return if (property.bedroomsAmount == 0) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

fun getFormattedAddress(property: Property): String{
    return "${property.address}\n" +
            "${property.city}\n" +
            "${property.postalCode}\n" +
            property.country
}

fun getAddressVisibility(property: Property): Int {
    return if (property.formattedAddress.isBlank()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

fun getPointsOfInterestVisibility(property: Property): Int {
    return if (property.pointOfInterestList.isEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

fun getMapVisibility(property: Property): Int {
    return if (property.latitude == 0.0 && property.longitude == 0.0) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

fun getAgentVisibility(property: Property): Int {
    return if (property.agentName.isBlank()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}