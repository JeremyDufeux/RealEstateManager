package com.openclassrooms.realestatemanager.mappers

import android.view.View
import com.openclassrooms.realestatemanager.models.Property


fun updatePriceVisibility(property: Property) {
    property.priceVisibility =
        if (property.price == null) { 
            View.GONE
        } else {
            View.VISIBLE
        }
}

fun updateSurfaceVisibility(property: Property) {
    property.surfaceVisibility =
        if (property.surface == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
}

fun updateDescriptionVisibility(property: Property) {
    property.descriptionVisibility =
        if (property.description.isBlank()) {
            View.GONE
        } else {
            View.VISIBLE
        }
}

fun updateRoomVisibility(property: Property) {
    property.roomsVisibility =
        if (property.roomsAmount == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
}

fun updateBathroomVisibility(property: Property) {
    property.bathroomsVisibility =
        if (property.bathroomsAmount == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
}

fun updateBedroomVisibility(property: Property) {
    property.bedroomsVisibility =
        if (property.bedroomsAmount == null) {
             View.GONE
        } else {
            View.VISIBLE
        }
}

fun updateFormattedAddress(property: Property){
    var address = ""
    property.apply {
        if (addressLine1.isNotEmpty()) address += addressLine1
        if (addressLine2.isNotEmpty()) address += "\n$addressLine2"
        if (city.isNotEmpty()) address += "\n$city"
        if (postalCode.isNotEmpty()) address += "\n$postalCode"
        if (country.isNotEmpty()) address += "\n$country"
    }
    property.formattedAddress = address
}

fun updateAddressVisibility(property: Property) {
    property.addressVisibility =
        if (property.formattedAddress.isBlank()) {
            View.GONE
        } else {
            View.VISIBLE
        }
}

fun updatePointsOfInterestVisibility(property: Property) {
    property.pointsOfInterestVisibility =
        if (property.pointOfInterestList.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
}

fun updateMapVisibility(property: Property) {
    property.mapVisibility =
        if (property.mapPictureUrl == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
}

fun updateSoldDateVisibility(property: Property) {
    property.soldDateVisibility =
        if (property.soldDate == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
}

fun updateAgentVisibility(property: Property) {
    property.agentVisibility =
        if (property.agentName.isBlank()) {
            View.GONE
        } else {
            View.VISIBLE
        }
}