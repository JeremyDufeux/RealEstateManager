package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.databaseEntites.PointOfInterestEntity
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest

class PointsOfInterestEntityToPointsOfInterestMapper {
    companion object{
        fun map(entitiesList: List<PointOfInterestEntity>): List<PointOfInterest>{
            val pointsOfInterest = mutableListOf<PointOfInterest>()

            for(entity in entitiesList){
                val pointOfInterest = PointOfInterest.valueOf(entity.description)
                pointsOfInterest.add(pointOfInterest)
            }
            return pointsOfInterest
        }
    }
}