package com.openclassrooms.realestatemanager.services

import com.openclassrooms.realestatemanager.models.Property

interface PropertyApiService {
    suspend fun propertyList() : List<Property>

    fun addProperty(property: Property)

    fun updateProperty(property: Property)
}