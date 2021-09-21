package com.openclassrooms.realestatemanager.api

import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.sealedClasses.State

interface PropertyApiService {
    suspend fun fetchProperties() : State
    suspend fun addProperty(property: Property): State
    suspend fun uploadMedia(mediaItem: MediaItem): State
    suspend fun deleteMedia(mediaItem: MediaItem): State
}