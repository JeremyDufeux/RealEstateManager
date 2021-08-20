package com.openclassrooms.realestatemanager.ui.list

import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(): ViewModel() {

    var propertyTypeList : MutableList<PropertyType> = ArrayList()
    var pointOfInterestList : MutableList<PointOfInterest> = ArrayList()

    var minPrice = 0f
    var maxPrice = 100f

    var minSurface = 0f
    var maxSurface = 100f

    var mediaAmount = 0
    var roomAmount = 0
    var bathroomAmount = 0
    var bedroomAmount = 0

    var city = ""

    var available = false
    var sold = false

    var postDate: Long? = null
    var soldDate: Long? = null

    fun resetValues() {
        propertyTypeList.clear()
        pointOfInterestList.clear()

        minPrice = 0f
        maxPrice = 100f

        minSurface = 0f
        maxSurface = 100f

        mediaAmount = 0
        roomAmount = 0
        bathroomAmount = 0
        bedroomAmount = 0

        city = ""

        available = false
        sold = false

        postDate = null
        soldDate = null
    }
}