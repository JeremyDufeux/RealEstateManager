package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.databaseEntites.MediaItemEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PointOfInterestEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyWithMediaItemAndPointsOfInterestEntity
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.ServerState

fun propertyToPropertyEntityMapper(property: Property): PropertyEntity {
    return PropertyEntity(
        propertyId = property.id,
        type = property.type,
        price = property.price,
        surface = property.surface,
        roomsAmount = property.roomsAmount,
        bathroomsAmount = property.bathroomsAmount,
        bedroomsAmount = property.bedroomsAmount,
        description = property.description,
        addressLine1 = property.addressLine1,
        addressLine2 = property.addressLine2,
        city = property.city,
        postalCode = property.postalCode,
        country = property.country,
        latitude = property.latitude,
        longitude = property.longitude,
        mapPictureUrl = property.mapPictureUrl,
        postDate = property.postDate,
        soldDate = property.soldDate,
        agentName = property.agentName,
        serverState = ServerState.SERVER
    )
}

fun propertyEntityToPropertyMapper(entity: PropertyWithMediaItemAndPointsOfInterestEntity): Property {
    val mediaList = mediaItemsEntityToMediaItemsMapper(entity.mediaList).sortedBy { it.id }
    val pointOfInterestList = pointsOfInterestEntityToPointsOfInterestMapper(entity.pointOfInterestList)

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
        postDate = entity.propertyEntity.postDate,
        soldDate = entity.propertyEntity.soldDate,
        agentName = entity.propertyEntity.agentName,
        mediaList = mediaList,
        pointOfInterestList = pointOfInterestList
    )
}

fun pointsOfInterestEntityToPointsOfInterestMapper(entitiesList: List<PointOfInterestEntity>): List<PointOfInterest>{
    val pointsOfInterest = mutableListOf<PointOfInterest>()

    for(entity in entitiesList){
        val pointOfInterest = PointOfInterest.valueOf(entity.description)
        pointsOfInterest.add(pointOfInterest)
    }
    return pointsOfInterest
}

fun mediaItemToMediaItemEntityMapper(mediaItem: MediaItem): MediaItemEntity {
    return MediaItemEntity(
        mediaId = mediaItem.id,
        propertyId = mediaItem.propertyId,
        url = mediaItem.url,
        description = mediaItem.description,
        fileType = mediaItem.fileType,
        serverState = ServerState.SERVER
    )
}

fun mediaItemsEntityToMediaItemsMapper(entitiesList: List<MediaItemEntity>): List<MediaItem>{
    val mediaList = mutableListOf<MediaItem>()

    for(entity in entitiesList){
        val mediaItem = MediaItem(
            id = entity.mediaId,
            propertyId = entity.propertyId,
            url = entity.url,
            description = entity.description,
            fileType = entity.fileType
        )
        mediaList.add(mediaItem)
    }
    return mediaList
}