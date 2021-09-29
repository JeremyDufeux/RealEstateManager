package com.openclassrooms.realestatemanager.mappers

import android.content.Context
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.UserData
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.enums.Unit
import com.openclassrooms.realestatemanager.models.ui.PropertyUiDetailsView
import com.openclassrooms.realestatemanager.utils.Utils.convertDollarsToEuros
import com.openclassrooms.realestatemanager.utils.Utils.convertSquareFootToSquareMeters
import com.openclassrooms.realestatemanager.utils.formatTimestampToString
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PropertyToPropertyUiDetailsViewMapper @Inject constructor(
    @ApplicationContext private val mContext: Context) {

    fun map(property: Property, userData: UserData): PropertyUiDetailsView {
        var address = ""
        property.apply {
            if (addressLine1.isNotEmpty()) address += "$addressLine1\n"
            if (addressLine2.isNotEmpty()) address += "$addressLine2\n"
            if (city.isNotEmpty()) address += "$city\n"
            if (postalCode.isNotEmpty()) address += "$postalCode\n"
            if (country.isNotEmpty()) address += country
        }

        val priceString = if (property.price != null) {
            if (userData.currency == Currency.DOLLAR) {
                String.format(
                    "%s%,d",
                    mContext.getString(Currency.DOLLAR.symbolResId),
                    property.price?.toInt()
                )
            } else {
                String.format(
                    "%,d%s",
                    convertDollarsToEuros(property.price!!).toInt(),
                    mContext.getString(Currency.EURO.symbolResId)
                )
            }
        } else {
            ""
        }

        val surfaceString = if (property.surface != null) {
            if (userData.unit == Unit.IMPERIAL) {
                String.format("%d %s", property.surface?.toInt(), mContext.getString(Unit.IMPERIAL.abbreviationRsId))
            } else {
                String.format(
                    "%.2f %s",
                    convertSquareFootToSquareMeters(property.surface!!),
                    mContext.getString(Unit.METRIC.abbreviationRsId)
                )
            }
        } else {
            ""
        }

        val roomsAmount = if (property.roomsAmount != null) property.roomsAmount.toString() else ""
        val bathroomsAmount =
            if (property.bathroomsAmount != null) property.bathroomsAmount.toString() else ""
        val bedroomsAmount =
            if (property.bedroomsAmount != null) property.bedroomsAmount.toString() else ""
        val soldDate =
            if (property.soldDate != null) formatTimestampToString(property.soldDate!!) else ""

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
            postDate = formatTimestampToString(property.postDate),
            soldDate = soldDate,
            agentName = property.agentName,
            mediaList = property.mediaList,
            pointOfInterestList = property.pointOfInterestList
        )
    }
}