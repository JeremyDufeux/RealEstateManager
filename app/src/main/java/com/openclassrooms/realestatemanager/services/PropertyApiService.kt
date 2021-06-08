package com.openclassrooms.realestatemanager.services

import com.openclassrooms.realestatemanager.models.Property

interface PropertyApiService {
    suspend fun getPropertyList() : List<Property>

    suspend fun getPropertyWithId(propertyId : String) : Property

    fun addProperty(property: Property)

    fun updateProperty(property: Property)
}