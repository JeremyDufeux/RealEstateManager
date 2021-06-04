package com.openclassrooms.realestatemanager.services

import com.openclassrooms.realestatemanager.extensions.generateProperties
import com.openclassrooms.realestatemanager.models.Property

class PropertyApiServiceImpl : PropertyApiService {
    private val mProperties : MutableList<Property> = generateProperties().toMutableList()

    override fun gePropertyList(): List<Property> {
        return mProperties
    }

    override fun addProperty(property: Property) {
        mProperties.add(property)
    }

    override fun updateProperty(property: Property) {
        val estateToUpdate = mProperties.find { it.id == property.id }
        val estateIndex = mProperties.indexOf(estateToUpdate)
        mProperties[estateIndex] = property
    }
}