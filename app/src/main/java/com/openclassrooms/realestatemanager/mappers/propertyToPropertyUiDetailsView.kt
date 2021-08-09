package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.ui.PropertyUiDetailsView
import com.openclassrooms.realestatemanager.utils.formatCalendarToString

fun propertyToPropertyUiDetailsView(property: Property): PropertyUiDetailsView {
    var address = ""
    property.apply {
        if (addressLine1.isNotEmpty()) address += addressLine1
        if (addressLine2.isNotEmpty()) address += "\n$addressLine2"
        if (city.isNotEmpty()) address += "\n$city"
        if (postalCode.isNotEmpty()) address += "\n$postalCode"
        if (country.isNotEmpty()) address += "\n$country"
    }
    val price = if(property.price != null) String.format("$%,d",property.price) else ""
    val surface = if(property.surface != null) String.format("%d sq ft",property.surface) else ""
    val roomsAmount = if(property.roomsAmount != null) property.roomsAmount.toString() else ""
    val bathroomsAmount = if(property.bathroomsAmount != null) property.bathroomsAmount.toString() else ""
    val bedroomsAmount = if(property.bedroomsAmount != null) property.bedroomsAmount.toString() else ""
    val soldDate = if(property.soldDate != null) formatCalendarToString(property.soldDate!!) else ""

    return PropertyUiDetailsView(
        id = property.id,
        type = property.type,
        price = price,
        surface = surface,
        roomsAmount = roomsAmount,
        bathroomsAmount = bathroomsAmount,
        bedroomsAmount = bedroomsAmount,
        description = property.description,
        address = address,
        latitude = property.latitude,
        longitude = property.longitude,
        mapPictureUrl = property.mapPictureUrl,
        postDate = formatCalendarToString(property.postDate),
        soldDate = soldDate,
        agentName = property.agentName,
        mediaList =  property.mediaList,
        pointOfInterestList = property.pointOfInterestList
    )
}