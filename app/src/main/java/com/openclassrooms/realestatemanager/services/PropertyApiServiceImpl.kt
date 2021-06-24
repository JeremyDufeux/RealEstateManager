package com.openclassrooms.realestatemanager.services

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val COLLECTION_NAME = "Properties"

class PropertyApiServiceImpl : PropertyApiService {
    private val mProperties : MutableList<Property> = mutableListOf()

    init {
    }

    private fun getPropertiesCollection() : CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
    }

    override suspend fun getPropertyList() : MutableList<Property> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            getPropertiesCollection().addSnapshotListener { value, _ ->
                if(value != null){
                    mProperties.clear()
                    for(document in value.documents){
                        val property : Property? = document.toObject(Property::class.java)
                        if (property != null) {
                            mProperties.add(property)
                        }
                    }
                    continuation.resume(mProperties)
                }
            }
        }
    }

    override suspend fun getPropertyWithId(propertyId : String): Property = withContext(Dispatchers.IO) {
        mProperties.find { it.id == propertyId}!!
    }

    override fun addProperty(property: Property) {
        getPropertiesCollection().document(property.id).set(property)
    }

    override fun updateProperty(property: Property) {
        val estateToUpdate = mProperties.find { it.id == property.id }
        val estateIndex = mProperties.indexOf(estateToUpdate)
        mProperties[estateIndex] = property
    }
}