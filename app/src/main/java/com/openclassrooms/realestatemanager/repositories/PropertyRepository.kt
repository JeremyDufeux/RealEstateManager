package com.openclassrooms.realestatemanager.repositories

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.services.PropertyApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor(private val mPropertyApiService: PropertyApiService){
    private var mProperties : MutableList<Property> = mutableListOf()

    fun getProperties() : Flow<List<Property>> =
        mPropertyApiService.getProperties().map { list ->
            mProperties = list as MutableList<Property>
            mProperties
        }

    fun getPropertyWithId(propertyId: String): Property {
        val estateToUpdate = mProperties.find { it.id == propertyId }
        val estateIndex = mProperties.indexOf(estateToUpdate)
        return mProperties[estateIndex]
    }

    fun addProperty(property: Property) {
        mPropertyApiService.addProperty(property)
    }

}