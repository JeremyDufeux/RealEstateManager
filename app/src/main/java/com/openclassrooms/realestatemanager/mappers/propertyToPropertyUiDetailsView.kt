package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.UserData
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.enums.Unit
import com.openclassrooms.realestatemanager.models.ui.PropertyUiDetailsView
import com.openclassrooms.realestatemanager.utils.Utils.convertDollarsToEuros
import com.openclassrooms.realestatemanager.utils.Utils.convertSquareFootToSquareMeters
import com.openclassrooms.realestatemanager.utils.formatCalendarToString

fun propertyToPropertyUiDetailsView(property: Property, userData: UserData): PropertyUiDetailsView {
    var address = ""
    property.apply {
        if (addressLine1.isNotEmpty()) address += addressLine1
        if (addressLine2.isNotEmpty()) address += "\n$addressLine2"
        if (city.isNotEmpty()) address += "\n$city"
        if (postalCode.isNotEmpty()) address += "\n$postalCode"
        if (country.isNotEmpty()) address += "\n$country"
    }

    val priceString = if(property.price != null){
        if(userData.currency == Currency.DOLLAR) {
            String.format("%s%,d", Currency.DOLLAR.symbol, property.price?.toInt())
        } else {
            String.format("%,d%s", convertDollarsToEuros(property.price!!).toInt(), Currency.EURO.symbol)
        }
    } else {
        ""
    }

    val surfaceString = if(property.price != null){
        if(userData.unit == Unit.IMPERIAL) {
            String.format("%d %s", property.surface?.toInt(), Unit.IMPERIAL.abbreviation)
        } else {
            String.format("%.2f %s", convertSquareFootToSquareMeters(property.surface!!), Unit.METRIC.abbreviation)
        }
    } else {
        ""
    }

    val roomsAmount = if(property.roomsAmount != null) property.roomsAmount.toString() else ""
    val bathroomsAmount = if(property.bathroomsAmount != null) property.bathroomsAmount.toString() else ""
    val bedroomsAmount = if(property.bedroomsAmount != null) property.bedroomsAmount.toString() else ""
    val soldDate = if(property.soldDate != null) formatCalendarToString(property.soldDate!!) else ""

    return PropertyUiDetailsView(
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