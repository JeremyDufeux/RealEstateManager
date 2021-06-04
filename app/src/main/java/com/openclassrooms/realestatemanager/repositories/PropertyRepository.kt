package com.openclassrooms.realestatemanager.repositories

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.services.PropertyApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor(val propertyApiService: PropertyApiService){

    fun getPropertyList(): List<Property> {
        return propertyApiService.gePropertyList()
    }

    fun addProperty(property: Property) {
        propertyApiService.addProperty(property)
    }

    fun updateProperty(property: Property) {
        propertyApiService.updateProperty(property)
    }
}