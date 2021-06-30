package com.openclassrooms.realestatemanager.services

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION_NAME = "Properties"

class PropertyApiService @Inject constructor() {

    private fun getPropertiesCollection() : CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
    }

    fun fetchProperties() : Flow<State<List<Property>>> = flow {
        try {
            val snapshot = getPropertiesCollection().get(Source.SERVER).await()
            val properties = snapshot.toObjects(Property::class.java)

            emit(State.Success(properties))
        }catch (e: Exception){
            emit(State.Failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun addProperty(property: Property) {
        getPropertiesCollection().document(property.id).set(property)
    }
}