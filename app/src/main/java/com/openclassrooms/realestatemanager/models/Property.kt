package com.openclassrooms.realestatemanager.models

import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import java.util.*

data class Property(
    val id: String = UUID.randomUUID().toString(),
    var type: PropertyType = PropertyType.FLAT,
    var price: Long = 0,
    var surface: String = "0 sq ft",
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
    var postDate: Long = 0,
    var soldDate: Long? = null,
    var agentName: String = ""
)