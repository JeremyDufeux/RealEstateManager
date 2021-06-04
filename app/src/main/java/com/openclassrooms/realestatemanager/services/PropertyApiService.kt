package com.openclassrooms.realestatemanager.services

import com.openclassrooms.realestatemanager.models.Property

interface PropertyApiService {
    fun gePropertyList() : List<Property>

    fun addProperty(property: Property)

    fun updateProperty(property: Property)
}