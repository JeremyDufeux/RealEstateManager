package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyEntity

class PropertyToPropertyEntityMapper {
    companion object{
        fun map(property: Property): PropertyEntity{
            return PropertyEntity(
                propertyId = property.id,
                type = property.type,
                price = property.price,
                surface = property.surface,
                roomsAmount = property.roomsAmount,
                bathroomsAmount = property.bathroomsAmount,
                bedroomsAmount = property.bedroomsAmount,
                description = property.description,
                address = property.address,
                city = property.city,
                postalCode = property.postalCode,
                country = property.country,
                latitude = property.latitude,
                longitude = property.longitude,
                mapPictureUrl = property.mapPictureUrl,
                available = property.available,
                postDate = property.postDate,
                soldDate = property.soldDate,
                agentName = property.agentName
            )
        }
    }
}