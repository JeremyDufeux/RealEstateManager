package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.ui.PropertyUiAddView

fun propertyToPropertyUiAddView(property: Property): PropertyUiAddView {
    val price = if(property.price != null) property.price.toString() else ""
    val surface = if(property.surface != null) property.surface.toString() else ""
    val roomsAmount = if(property.roomsAmount != null) property.roomsAmount.toString() else ""
    val bathroomsAmount = if(property.bathroomsAmount != null) property.bathroomsAmount.toString() else ""
    val bedroomsAmount = if(property.bedroomsAmount != null) property.bedroomsAmount.toString() else ""

    return PropertyUiAddView(
        id = property.id,
        type = property.type,
        price = price,
        surface = surface,
        roomsAmount = roomsAmount,
        bathroomsAmount = bathroomsAmount,
        bedroomsAmount = bedroomsAmount,
        description = property.description,
        addressLine1 = property.addressLine1,
        addressLine2 = property.addressLine2,
        city = property.city,
        postalCode = property.postalCode,
        country = property.country,
        postDate = property.postDate,
        soldDate = property.soldDate,
        agentName = property.agentName,
        mediaList =  property.mediaList,
        pointOfInterestList = property.pointOfInterestList
    )
}