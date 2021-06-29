package com.openclassrooms.realestatemanager.services

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

private const val TAG = "PropertyApiService"
private const val COLLECTION_NAME = "Properties"

class PropertyApiService @Inject constructor() {

    private fun getPropertiesCollection() : CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
    }

    fun getProperties() : Flow<List<Property>> = callbackFlow {

        val subscription = getPropertiesCollection().addSnapshotListener { snapshot, _ ->
            if(snapshot != null) {
                val properties = mutableListOf<Property>()

                for (document in snapshot.documents) {
                    val property: Property? = document.toObject(Property::class.java)
                    if (property != null) {
                        properties.add(property)
                    }
                }
                offer(properties)
            } else{
                return@addSnapshotListener
            }
        }
        awaitClose { subscription.remove() }
    }

    fun addProperty(property: Property) {
        getPropertiesCollection().document(property.id).set(property)
    }

}