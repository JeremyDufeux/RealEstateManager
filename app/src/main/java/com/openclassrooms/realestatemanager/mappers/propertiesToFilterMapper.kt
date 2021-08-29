package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.PropertyFilter
import com.openclassrooms.realestatemanager.models.UserData


fun propertiesToFilterMapper(properties: List<Property>, userData: UserData): PropertyFilter { // TODO User data
    var minPrice = 0L
    var maxPrice = 0L
    var minSurface = 0L
    var maxSurface = 0L

    for(property in properties){
        if(property.price != null) {
            if (property.price!! < minPrice) {
                minPrice = property.price!!.toLong()
            }
            if (property.price!! > maxPrice) {
                maxPrice = property.price!!.toLong()
            }
        }
        if(property.surface != null) {
            if (property.surface!! < minSurface) {
                minSurface = property.surface!!.toLong()
            }
            if (property.surface!! > maxSurface) {
                maxSurface = property.surface!!.toLong()
            }
        }
    }

    return PropertyFilter(
        minPrice = minPrice,
        maxPrice = maxPrice,
        minSurface = minSurface,
        maxSurface = maxSurface)
}