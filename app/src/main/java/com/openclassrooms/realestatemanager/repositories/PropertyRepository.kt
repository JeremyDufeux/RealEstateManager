package com.openclassrooms.realestatemanager.repositories

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.services.PropertyApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor(private val propertyApiService: PropertyApiService){

    suspend fun propertyList() : List<Property> = propertyApiService.propertyList()

    fun addProperty(property: Property) {
        propertyApiService.addProperty(property)
    }

    fun updateProperty(property: Property) {
        propertyApiService.updateProperty(property)
    }
}