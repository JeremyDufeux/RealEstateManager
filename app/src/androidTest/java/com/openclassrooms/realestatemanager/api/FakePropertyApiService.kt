package com.openclassrooms.realestatemanager.api

import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.utils.generateOnlineProperties
import kotlinx.coroutines.delay

class FakePropertyApiService: PropertyApiService {
    private val mPropertyList = mutableListOf<Property>()

    init {
        mPropertyList.addAll(generateOnlineProperties())
    }

    override suspend fun fetchProperties(): State {
        delay(200)
        return State.Download.DownloadSuccess(mPropertyList)
    }

    override suspend fun addProperty(property: Property): State {
        val oldProperty = mPropertyList.firstOrNull { it.id == property.id }
        if(oldProperty != null){
            val index = mPropertyList.indexOf(oldProperty)
            mPropertyList[index] = property
        } else {
            mPropertyList.add(property)
        }
        delay(100)
        return State.Upload.UploadSuccess.Empty
    }

    override suspend fun uploadMedia(mediaItem: MediaItem): State {
        delay(100)
        return State.Upload.UploadSuccess.Url("Fake Url")
    }

    override suspend fun deleteMedia(mediaItem: MediaItem): State {
        delay(100)
        return State.Upload.UploadSuccess.Empty
    }
}