package com.openclassrooms.realestatemanager.models

import java.util.*

data class Property(
    val id: String? = UUID.randomUUID().toString(),
    var type : PropertyTypes,
    var price : Int,
    var area : Int,
    var roomsAmount : Int,
    var description : String,
    var picturesUriList : List<String>,
    var address : String,
    var city : String,
    var postalCode : String,
    var country : String,
    var pointOfInterest : List<PointsOfInterests>,
    var available : Boolean,
    var saleDate : Calendar? = null,
    var dateOfSale : Calendar? = null,
    var agentName : String
)