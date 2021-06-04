package com.openclassrooms.realestatemanager.repositories

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.services.PropertyApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor(private val propertyApiService: PropertyApiService){

    val propertyList: Flow<List<Property>> = propertyApiService.propertyList

    fun addProperty(property: Property) {
        propertyApiService.addProperty(property)
    }

    fun updateProperty(property: Property) {
        propertyApiService.updateProperty(property)
    }
}