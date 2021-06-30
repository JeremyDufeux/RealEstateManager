package com.openclassrooms.realestatemanager.repositories

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.State
import com.openclassrooms.realestatemanager.services.PropertyApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor(
    private val mPropertyApiService: PropertyApiService){

    private var mProperties: MutableList<Property> = mutableListOf()
    private val _propertiesFlow = MutableStateFlow<State<List<Property>>>(State.Loading())
    val propertiesFlow = _propertiesFlow.asStateFlow()

    suspend fun fetchProperties() {
        mPropertyApiService.fetchProperties()
            .collect { result ->
                if(result is State.Success){
                    mProperties = result.value as MutableList<Property>
                }
                _propertiesFlow.value = result
        }
    }

    fun getPropertyWithId(propertyId: String): Property {
        val estateToUpdate = mProperties.find {it.id == propertyId }
        val estateIndex = mProperties.indexOf(estateToUpdate)
        return mProperties[estateIndex]
    }

    suspend fun addPropertyAndFetch(property: Property) {
        addProperty(property)
        fetchProperties()
    }

    private fun addProperty(property: Property) {
        mPropertyApiService.addProperty(property)
    }
}