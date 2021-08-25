package com.openclassrooms.realestatemanager.models

import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType

data class PropertyFilter(
    var minPrice: Double,
    var maxPrice: Double,
    var minSurface: Double,
    var maxSurface: Double,
    var mediasAmount: Int = 0,
    var roomsAmount: Int = 0,
    var bathroomsAmount: Int = 0,
    var bedroomsAmount: Int = 0,
    var city: String = "",
    var available: Boolean = false,
    var sold: Boolean = false,
    var postDate: Long = 0,
    var soldDate: Long = 0,
    var propertyTypeList: MutableList<PropertyType> = ArrayList(),
    var pointOfInterestList: MutableList<PointOfInterest> = ArrayList()
){
    fun resetFilters(){
        minPrice = 0.0
        maxPrice = 100.0
        minSurface = 0.0
        maxSurface = 100.0
        mediasAmount = 0
        roomsAmount = 0
        bathroomsAmount = 0
        bedroomsAmount = 0
        city = ""
        available = false
        sold = false
        postDate = 0
        soldDate = 0
        propertyTypeList.clear()
        pointOfInterestList.clear()
    }
}