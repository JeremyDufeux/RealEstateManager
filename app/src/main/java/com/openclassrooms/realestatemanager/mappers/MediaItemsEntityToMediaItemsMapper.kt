package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.databaseEntites.MediaItemEntity

class MediaItemsEntityToMediaItemsMapper {
    companion object{
        fun map(entitiesList: List<MediaItemEntity>): List<MediaItem>{
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
    }
}