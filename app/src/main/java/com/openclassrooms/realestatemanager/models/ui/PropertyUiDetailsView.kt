package com.openclassrooms.realestatemanager.models.ui

import android.view.View
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType

data class PropertyUiDetailsView(
    var id: String = "",
    var type: PropertyType = PropertyType.FLAT,
    var price: String,
    var surface: String,
    var roomsAmount: String,
    var bathroomsAmount: String,
    var bedroomsAmount: String,
    var description: String = "",
    var address: String = "",
    var latitude: Double? = null,
    var longitude: Double? = null,
    var mapPictureUrl: String? = null,
    var postDate: String,
    var soldDate: String,
    var agentName: String = "",

    var mediaList: List<MediaItem> = listOf(),
    var pointOfInterestList: List<PointOfInterest> = listOf(),
){
    val priceVisibility: Int = if (price.isBlank()){
        View.GONE
    } else {
        View.VISIBLE
    }

    val surfaceVisibility: Int = if (surface.isBlank()){
        View.GONE
    } else {
        View.VISIBLE
    }

    val roomsVisibility: Int = if (roomsAmount.isBlank()){
        View.GONE
    } else {
        View.VISIBLE
    }

    val bathroomsVisibility: Int = if (bathroomsAmount.isBlank()){
        View.GONE
    } else {
        View.VISIBLE
    }

    val bedroomsVisibility: Int = if (bedroomsAmount.isBlank()){
        View.GONE
    } else {
        View.VISIBLE
    }

    val mapVisibility: Int = if(mapPictureUrl == null) {
        View.GONE
    } else {
        View.VISIBLE
    }

    val soldDateVisibility: Int = if (soldDate.isBlank()) {
        View.GONE
    } else {
        View.VISIBLE
    }

    val descriptionVisibility: Int = if (description.isBlank()){
        View.GONE
    } else {
        View.VISIBLE
    }

    val addressVisibility: Int = if (address.isBlank()){
        View.GONE
    } else {
        View.VISIBLE
    }

    val agentVisibility: Int = if (agentName.isBlank()) {
        View.GONE
    } else {
        View.VISIBLE
    }

    val mediaVisibility: Int = if (mediaList.isEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }

    val pointsOfInterestVisibility: Int = if (pointOfInterestList.isEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}