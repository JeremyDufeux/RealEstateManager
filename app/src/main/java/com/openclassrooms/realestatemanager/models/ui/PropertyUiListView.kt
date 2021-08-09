package com.openclassrooms.realestatemanager.models.ui

import android.view.View
import com.openclassrooms.realestatemanager.models.enums.PropertyType

data class PropertyUiListView(
    val id: String = "",
    val type: PropertyType = PropertyType.FLAT,
    val price: String,
    val city: String? = null,
    val pictureUrl: String
){
    val priceVisibility: Int = if (price.isBlank()){
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}
