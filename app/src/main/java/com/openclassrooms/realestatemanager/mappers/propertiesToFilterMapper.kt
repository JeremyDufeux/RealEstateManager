package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.PropertyFilter
import com.openclassrooms.realestatemanager.models.UserData


fun propertiesToFilterMapper(properties: List<Property>, userData: UserData): PropertyFilter { // TODO User data
    var minPrice = 0.0
    var maxPrice = 0.0
    var minSurface = 0.0
    var maxSurface = 0.0

    for(property in properties){
        if(property.price != null) {
            if (property.price!! < minPrice) {
                minPrice = property.price!!
            }
            if (property.price!! > maxPrice) {
                maxPrice = property.price!!
            }
        }
        if(property.surface != null) {
            if (property.surface!! < minSurface) {
                minSurface = property.surface!!
            }
            if (property.surface!! > maxSurface) {
                maxSurface = property.surface!!
            }
        }
    }

    return PropertyFilter(
        minPrice = minPrice,
        maxPrice = maxPrice,
        minSurface = minSurface,
        maxSurface = maxSurface)
}