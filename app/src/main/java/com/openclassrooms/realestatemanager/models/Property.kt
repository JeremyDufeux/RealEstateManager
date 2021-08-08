package com.openclassrooms.realestatemanager.models

import android.view.View
import com.google.firebase.firestore.Exclude
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType

data class Property(
    var id: String = "",
    var type: PropertyType = PropertyType.FLAT,
    var price: Long? = null, // In dollars
    var surface: Int? = null, // In square feet
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
    var available: Boolean = true,
    var postDate: Long = 0, // Timestamp
    var soldDate: Long? = null, // Timestamp
    var agentName: String = "",

    @Exclude @set:Exclude @get:Exclude
    var mediaList: List<MediaItem> = listOf(),

    @Exclude @set:Exclude @get:Exclude
    var priceVisibility: Int = View.VISIBLE,
    @Exclude @set:Exclude @get:Exclude
    var surfaceVisibility: Int = View.VISIBLE,
    @Exclude @set:Exclude @get:Exclude
    var descriptionVisibility: Int = View.VISIBLE,
    @Exclude @set:Exclude @get:Exclude
    var roomsVisibility: Int = View.VISIBLE,
    @Exclude @set:Exclude @get:Exclude
    var bathroomsVisibility: Int = View.VISIBLE,
    @Exclude @set:Exclude @get:Exclude
    var bedroomsVisibility: Int = View.VISIBLE,
    @Exclude @set:Exclude @get:Exclude
    var formattedAddress: String = "",
    @Exclude @set:Exclude @get:Exclude
    var addressVisibility: Int = View.VISIBLE,
    @Exclude @set:Exclude @get:Exclude
    var pointsOfInterestVisibility: Int = View.VISIBLE,
    @Exclude @set:Exclude @get:Exclude
    var mapVisibility: Int = View.VISIBLE,
    @Exclude @set:Exclude @get:Exclude
    var soldDateVisibility: Int = View.VISIBLE,
    @Exclude @set:Exclude @get:Exclude
    var agentVisibility: Int = View.VISIBLE,
)