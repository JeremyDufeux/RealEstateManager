package com.openclassrooms.realestatemanager.models

import java.util.*

data class Property(
    val id: String? = UUID.randomUUID().toString(),
    var type: PropertyType,
    var price: Int,
    var surface: String,
    var roomsAmount: Int,
    var bathroomsAmount: Int,
    var bedroomsAmount: Int,
    var description: String,
    var picturesUriList: List<Pair<String, String?>>,
    var address: String,
    var city: String,
    var postalCode: String,
    var country: String,
    var longitude: Double,
    var latitude: Double,
    var mapPictureUrl: String,
    var pointOfInterest: List<PointsOfInterest>,
    var available: Boolean,
    var saleDate: Calendar,
    var dateOfSale: Calendar? = null,
    var agentName: String
)