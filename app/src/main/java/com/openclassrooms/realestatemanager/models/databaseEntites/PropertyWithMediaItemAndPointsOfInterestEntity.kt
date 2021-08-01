package com.openclassrooms.realestatemanager.models.databaseEntites

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PropertyWithMediaItemAndPointsOfInterestEntity(
    @Embedded val propertyEntity: PropertyEntity,
    @Relation(
        parentColumn = "propertyId",
        entityColumn = "propertyId"
    )
    val mediaList: List<MediaItemEntity>,

    @Relation(
        parentColumn = "propertyId",
        entityColumn = "description",
        associateBy = Junction(PropertyPointOfInterestCrossRef::class)
    )
    val pointOfInterestList: List<PointOfInterestEntity>
)
