package com.openclassrooms.realestatemanager.models.databaseEntites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "points_of_interest")
data class PointOfInterestEntity(
    @PrimaryKey
    val description : String
    )