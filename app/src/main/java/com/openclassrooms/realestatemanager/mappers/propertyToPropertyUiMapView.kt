package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.ui.PropertyUiMapView

fun propertyToPropertyUiMapView(properties: List<Property>): List<PropertyUiMapView>{
    val propertiesUi = mutableListOf<PropertyUiMapView>()
    for (property in properties){
        propertiesUi.add(
            PropertyUiMapView(
                id = property.id,
                latitude = property.latitude,
                longitude = property.longitude
            )
        )
    }
    return propertiesUi
}