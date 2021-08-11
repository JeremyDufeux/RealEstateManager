package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.UserData
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.enums.Unit
import com.openclassrooms.realestatemanager.models.ui.PropertyUiAddView
import com.openclassrooms.realestatemanager.utils.Utils.convertDollarsToEuros
import com.openclassrooms.realestatemanager.utils.Utils.convertSquareFootToSquareMeters

fun propertyToPropertyUiAddView(property: Property, userData: UserData): PropertyUiAddView {
    val roomsAmount = if(property.roomsAmount != null) property.roomsAmount.toString() else ""
    val bathroomsAmount = if(property.bathroomsAmount != null) property.bathroomsAmount.toString() else ""
    val bedroomsAmount = if(property.bedroomsAmount != null) property.bedroomsAmount.toString() else ""

    val priceString = if(property.price != null){
        if(userData.currency == Currency.DOLLAR) {
            String.format("%d", property.price?.toInt())
        } else {
            String.format("%d", convertDollarsToEuros(property.price!!).toInt())
        }
    } else {
        ""
    }

    val surfaceString = if(property.price != null){
        if(userData.unit == Unit.IMPERIAL) {
            String.format("%d", property.surface?.toInt())
        } else {
            String.format("%.2f", convertSquareFootToSquareMeters(property.surface!!))
        }
    } else {
        ""
    }

    return PropertyUiAddView(
        id = property.id,
        type = property.type,
        price = property.price,
        priceString = priceString,
        surface = property.surface,
        surfaceString = surfaceString,
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