package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.ui.PropertyUiListView

fun propertyToPropertyUiListView(properties: List<Property>): List<PropertyUiListView>{
    val propertiesUi = mutableListOf<PropertyUiListView>()
    for (property in properties){
        val price = if(property.price != null) String.format("$%,d", property.price) else ""

        propertiesUi.add(
            PropertyUiListView(
                id = property.id,
                type = property.type,
                price = price,
                city = property.city,
                pictureUrl = property.mediaList[0].url
            )
        )
    }
    return propertiesUi
}