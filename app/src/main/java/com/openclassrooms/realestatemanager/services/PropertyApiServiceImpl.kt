package com.openclassrooms.realestatemanager.services

import com.openclassrooms.realestatemanager.extensions.generateProperties
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class PropertyApiServiceImpl : PropertyApiService {
    private val mProperties : MutableList<Property> = generateProperties().toMutableList()

    override suspend fun propertyList() = withContext(Dispatchers.IO) {
            val propertyList = generateProperties()
            delay(1500)
            propertyList
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