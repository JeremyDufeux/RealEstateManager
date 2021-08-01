package com.openclassrooms.realestatemanager.models.databaseEntites

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName= "properties_point_of_interest_cross_ref",
    primaryKeys = ["propertyId", "description"],
    indices = [Index(value = ["description"])])
data class PropertyPointOfInterestCrossRef(
    val propertyId: String,
    val description: String
)
