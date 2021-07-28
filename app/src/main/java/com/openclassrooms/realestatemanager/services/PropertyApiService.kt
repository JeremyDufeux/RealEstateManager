package com.openclassrooms.realestatemanager.services

import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.openclassrooms.realestatemanager.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

private const val COLLECTION_PROPERTIES_NAME = "Properties"

class PropertyApiService @Inject constructor() {

    private fun getPropertiesCollection() : CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_PROPERTIES_NAME)
    }

    fun fetchProperties() : Flow<State> = flow {
        try {
            val snapshot = getPropertiesCollection().get(Source.SERVER).await()
            val properties = mutableListOf<Property>()

            for (doc in snapshot.documents){
                val roomAmount = (doc["roomsAmount"] as Long).toInt()
                val bathroomsAmount = (doc["bathroomsAmount"] as Long).toInt()
                val bedroomsAmount = (doc["bedroomsAmount"] as Long).toInt()
                val type = PropertyType.valueOf(doc["type"] as String)
                val poiList = mutableListOf<PointsOfInterest>()

                for (item in doc["pointOfInterest"] as List<*> ){
                    poiList.add(PointsOfInterest.valueOf(item as String))
                }

                val medialList = mutableListOf<MediaItem>()

                for (item in doc["mediaList"] as List<*>){
                    val map = item as HashMap<*, *>
                    val id = map["id"] as String
                    val url = map["url"] as String
                    val description = map["description"] as String?
                    val fileType = FileType.valueOf(map["fileType"] as String)

                    medialList.add(MediaItem(id, url, description, fileType))
                }

                val property = Property(
                    id = doc["id"] as String,
                    type = type,
                    price = doc["price"] as Long,
                    surface = doc["surface"] as String,
                    roomsAmount = roomAmount,
                    bathroomsAmount = bathroomsAmount,
                    bedroomsAmount = bedroomsAmount,
                    description = doc["description"] as String,
                    mediaList = medialList,
                    address = doc["address"] as String,
                    city = doc["city"] as String,
                    postalCode = doc["postalCode"] as String,
                    country = doc["country"] as String,
                    latitude = doc["latitude"] as Double,
                    longitude = doc["longitude"] as Double,
                    mapPictureUrl = doc["mapPictureUrl"] as String,
                    pointOfInterest = poiList,
                    available = doc["available"] as Boolean,
                    saleDate = doc["saleDate"] as Long,
                    dateOfSale = doc["dateOfSale"] as Long?,
                    agentName = doc["agentName"] as String
                )
                properties.add(property)
            }

            emit(State.Download.DownloadSuccess(properties))
        }catch (e: Exception){
            emit(State.Download.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    fun addProperty(property: Property) {
        getPropertiesCollection().document(property.id).set(property)
    }

    suspend fun uploadMedia(url: String): State{
        return try {
            val uuid = UUID.randomUUID().toString()

            val mediaRef: StorageReference = FirebaseStorage.getInstance().getReference(uuid)
            mediaRef.putFile(Uri.parse(url)).await()

            State.Upload.UploadSuccess(mediaRef.downloadUrl.await().toString())
        } catch (e: java.lang.Exception){
            State.Upload.Error(e)
        }
    }
}