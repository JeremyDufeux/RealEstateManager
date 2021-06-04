package com.openclassrooms.realestatemanager.services

import com.openclassrooms.realestatemanager.models.Property
import kotlinx.coroutines.flow.Flow

interface PropertyApiService {
    val propertyList : Flow<List<Property>>

    fun addProperty(property: Property)

    fun updateProperty(property: Property)
}