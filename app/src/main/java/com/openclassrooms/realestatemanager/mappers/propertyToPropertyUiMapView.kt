package com.openclassrooms.realestatemanager.mappers

import android.content.Context
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.ui.PropertyUiMapView
import com.openclassrooms.realestatemanager.utils.Utils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PropertyToPropertyUiMapViewMapper @Inject constructor(
    @ApplicationContext private val mContext: Context
) {

    fun map(properties: List<Property>, currency: Currency): List<PropertyUiMapView> {
        val propertiesUi = mutableListOf<PropertyUiMapView>()


        for (property in properties) {
            val priceString = if (property.price != null) {
                if (currency == Currency.DOLLAR) {
                    String.format(
                        "%s%,d",
                        mContext.getString(Currency.DOLLAR.symbolResId),
                        property.price?.toInt()
                    )
                } else {
                    String.format(
                        "%,d%s",
                        Utils.convertDollarsToEuros(property.price!!).toInt(),
                        mContext.getString(Currency.EURO.symbolResId)
                    )
                }
            } else {
                ""
            }

            propertiesUi.add(
                PropertyUiMapView(
                    id = property.id,
                    latitude = property.latitude,
                    longitude = property.longitude,
                    priceString = priceString
                )
            )
        }
        return propertiesUi
    }
}