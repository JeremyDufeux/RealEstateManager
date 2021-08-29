package com.openclassrooms.realestatemanager.models

import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType

data class PropertyFilter(
    var isActive: Boolean = false,
    val minPrice: Long,
    val maxPrice: Long,
    val minSurface: Long,
    val maxSurface: Long,
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
    var selectedMinPrice = minPrice
    var selectedMaxPrice = maxPrice
    var selectedMinSurface = minSurface
    var selectedMaxSurface = maxSurface

    fun clearFilters(){
        isActive = false
        selectedMinPrice = minPrice
        selectedMaxPrice = maxPrice
        selectedMinSurface = minSurface
        selectedMaxSurface = maxSurface
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