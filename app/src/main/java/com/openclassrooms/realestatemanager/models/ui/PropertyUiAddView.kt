package com.openclassrooms.realestatemanager.models.ui

import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType

data class PropertyUiAddView(
    var id: String = "",
    var type: PropertyType = PropertyType.FLAT,
    val price: Double?,
    val priceString: String,
    var surface: Double?,
    val surfaceString: String,
    var roomsAmount: String,
    var bathroomsAmount: String,
    var bedroomsAmount: String,
    var description: String = "",
    var addressLine1: String = "",
    var addressLine2: String = "",
    var city: String = "",
    var postalCode: String = "",
    var country: String = "",
    var postDate: Long = 0,
    var soldDate: Long? = null,
    var agentName: String = "",
    var mediaList: List<MediaItem> = listOf(),
    var pointOfInterestList: List<PointOfInterest> = listOf(),
)