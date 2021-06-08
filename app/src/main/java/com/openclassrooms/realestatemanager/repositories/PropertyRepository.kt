package com.openclassrooms.realestatemanager.repositories

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.services.PropertyApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor(private val propertyApiService: PropertyApiService){

    suspend fun getPropertyList() : List<Property> = propertyApiService.getPropertyList()

    suspend fun getPropertyWithId(propertyId: String): Property =propertyApiService.getPropertyWithId(propertyId)

    fun addProperty(property: Property) {
        propertyApiService.addProperty(property)
    }

    fun updateProperty(property: Property) {
        propertyApiService.updateProperty(property)
    }
}