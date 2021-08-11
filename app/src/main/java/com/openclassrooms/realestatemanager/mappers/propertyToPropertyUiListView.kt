package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.ui.PropertyUiListView
import com.openclassrooms.realestatemanager.utils.Utils.convertDollarsToEuros

fun propertyToPropertyUiListView(properties: List<Property>, currency: Currency): List<PropertyUiListView>{
    val propertiesUi = mutableListOf<PropertyUiListView>()
    for (property in properties){
        val priceString =  if(property.price != null){
            if(currency == Currency.DOLLAR) {
                String.format("%s%,d", Currency.DOLLAR.symbol, property.price?.toInt())
            } else {
                String.format("%,d%s", convertDollarsToEuros(property.price!!).toInt(), Currency.EURO.symbol)
            }
        } else {
            ""
        }
        val sold = property.soldDate != null

        propertiesUi.add(
            PropertyUiListView(
                id = property.id,
                type = property.type,
                price = property.price,
                priceString = priceString,
                city = property.city,
                pictureUrl = property.mediaList[0].url,
                sold = sold
            )
        )
    }
    return propertiesUi
}