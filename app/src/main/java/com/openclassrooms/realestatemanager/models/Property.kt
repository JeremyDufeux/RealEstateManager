package com.openclassrooms.realestatemanager.models

import com.google.firebase.firestore.Exclude
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType

data class Property(
    var id: String = "",
    var type: PropertyType = PropertyType.FLAT,
    var price: Double? = null, // In dollars
    var surface: Double? = null, // In square feet
    var roomsAmount: Int? = null,
    var bathroomsAmount: Int? = null,
    var bedroomsAmount: Int? = null,
    var description: String = "",
    var addressLine1: String = "",
    var addressLine2: String = "",
    var city: String = "",
    var postalCode: String = "",
    var country: String = "",
    var latitude: Double? = null,
    var longitude: Double? = null,
    var mapPictureUrl: String? = null,
    var pointOfInterestList: List<PointOfInterest> = listOf(),
    var postDate: Long = 0, // Timestamp
    var soldDate: Long? = null, // Timestamp
    var agentName: String = "",

    @Exclude @set:Exclude @get:Exclude
    var mediaList: List<MediaItem> = listOf(),
)