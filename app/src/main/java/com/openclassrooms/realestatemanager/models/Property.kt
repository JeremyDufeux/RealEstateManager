package com.openclassrooms.realestatemanager.models

import android.view.View
import com.google.firebase.firestore.Exclude
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import java.util.*

data class Property(
    val id: String = UUID.randomUUID().toString(),
    var type: PropertyType = PropertyType.FLAT,
    var price: Long = 0, // In dollars
    var surface: Int = 0, // In square feet
    var roomsAmount: Int = 0,
    var bathroomsAmount: Int = 0,
    var bedroomsAmount: Int = 0,
    var description: String = "",
    var mediaList: List<MediaItem> = listOf(),
    var address: String = "",
    var city: String = "",
    var postalCode: String = "",
    var country: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var mapPictureUrl: String = "",
    var pointOfInterestList: List<PointOfInterest> = listOf(),
    var available: Boolean = true,
    var postDate: Long = 0, // Timestamp
    var soldDate: Long? = null, // Timestamp
    var agentName: String = "",

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
    var agentVisibility: Int = View.VISIBLE,
)