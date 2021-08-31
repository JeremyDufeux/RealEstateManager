package com.openclassrooms.realestatemanager.models.ui

import android.view.View
import com.openclassrooms.realestatemanager.models.enums.PropertyType

data class PropertyUiListView(
    val id: String = "",
    val type: PropertyType = PropertyType.FLAT,
    val price: Double?,
    val priceString: String,
    val city: String? = null,
    val pictureUrl: String,
    val sold: Boolean
){
    val priceVisibility: Int = if (priceString.isBlank()){
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}
