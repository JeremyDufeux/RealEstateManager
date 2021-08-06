package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyWithMediaItemAndPointsOfInterestEntity

class PropertyEntityToPropertyMapper {
    companion object{
        fun map(entity: PropertyWithMediaItemAndPointsOfInterestEntity): Property {
            val mediaList = MediaItemsEntityToMediaItemsMapper.map(entity.mediaList)
            val pointOfInterestList =
                PointsOfInterestEntityToPointsOfInterestMapper.map(entity.pointOfInterestList)

            return Property(
                id = entity.propertyEntity.propertyId,
                type = entity.propertyEntity.type,
                price = entity.propertyEntity.price,
                surface = entity.propertyEntity.surface,
                roomsAmount = entity.propertyEntity.roomsAmount,
                bathroomsAmount = entity.propertyEntity.bathroomsAmount,
                bedroomsAmount = entity.propertyEntity.bedroomsAmount,
                description = entity.propertyEntity.description,
                addressLine1 = entity.propertyEntity.addressLine1,
                addressLine2 = entity.propertyEntity.addressLine2,
                city = entity.propertyEntity.city,
                postalCode = entity.propertyEntity.postalCode,
                country = entity.propertyEntity.country,
                latitude = entity.propertyEntity.latitude,
                longitude = entity.propertyEntity.longitude,
                mapPictureUrl = entity.propertyEntity.mapPictureUrl,
                available = entity.propertyEntity.available,
                postDate = entity.propertyEntity.postDate,
                soldDate = entity.propertyEntity.soldDate,
                agentName = entity.propertyEntity.agentName,
                mediaList = mediaList,
                pointOfInterestList = pointOfInterestList
            )
        }
    }
}