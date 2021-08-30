package com.openclassrooms.realestatemanager.models

import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType

data class PropertyFilter(
    var minPrice: Long = 0,
    var maxPrice: Long = 0,
    var minSurface: Long = 0,
    var maxSurface: Long = 0,
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
    var userData = UserData()

    fun clearFilters(){
        minPrice = 0
        maxPrice = 0
        minSurface = 0
        maxSurface = 0
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

    fun isDefaultValues(): Boolean{
        return minPrice == 0L
                && maxPrice == 0L
                && minSurface == 0L
                && maxSurface == 0L
                && mediasAmount == 0
                && roomsAmount == 0
                && bathroomsAmount == 0
                && bedroomsAmount == 0
                && city.isEmpty()
                && !available
                && !sold
                && propertyTypeList.isEmpty()
                && pointOfInterestList.isEmpty()
    }
}