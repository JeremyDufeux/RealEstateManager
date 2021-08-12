package com.openclassrooms.realestatemanager.api

import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION_PROPERTIES_NAME = "Properties"
private const val COLLECTION_MEDIAS_NAME = "Medias"

class PropertyApiService @Inject constructor() {

    private fun getPropertiesCollection() : CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_PROPERTIES_NAME)
    }

    suspend fun fetchProperties() : State {
        return try {
            val snapshot = getPropertiesCollection().get(Source.SERVER).await()
            val properties = snapshot.toObjects(Property::class.java)

            for (property in properties){
                val mediaSnapshot = getPropertiesCollection().document(property.id).collection(COLLECTION_MEDIAS_NAME).get(Source.SERVER).await()
                val mediaItems = mediaSnapshot.toObjects(MediaItem::class.java)

                property.mediaList = mediaItems
            }

            State.Download.DownloadSuccess(properties)
        }catch (e: Exception){
            State.Download.Error(e)
        }
    }

    fun addProperty(property: Property): State{
        return try {
            getPropertiesCollection().document(property.id).set(property)
            for (media in property.mediaList) {
                getPropertiesCollection().document(property.id).collection(COLLECTION_MEDIAS_NAME).document(media.id).set(media)
            }
            State.Upload.UploadSuccess.Empty
        }catch (e: Exception){
            State.Upload.Error(e)
        }
    }

    suspend fun uploadMedia(mediaItem: MediaItem): State {
        return try {
            val ref =  "${mediaItem.propertyId}/${mediaItem.id}"
            val mediaRef: StorageReference = FirebaseStorage.getInstance().reference.child(ref)
            val file = Uri.parse(mediaItem.url)

            mediaRef.putFile(file).await()

            val url = mediaRef.downloadUrl.await().toString()
            State.Upload.UploadSuccess.Url(url)
        } catch (e: java.lang.Exception){
            State.Upload.Error(e)
        }
    }

    suspend fun deleteMedia(mediaItem: MediaItem): State {
        return try {
            val ref =  "${mediaItem.propertyId}/${mediaItem.id}"
            val mediaRef: StorageReference = FirebaseStorage.getInstance().reference.child(ref)

            mediaRef.delete().await()
            getPropertiesCollection().document(mediaItem.propertyId).collection(COLLECTION_MEDIAS_NAME).document(mediaItem.id).delete().await()

            State.Upload.UploadSuccess.Empty
        } catch (e: java.lang.Exception){
            State.Upload.Error(e)
        }
    }
}