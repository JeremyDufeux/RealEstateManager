package com.openclassrooms.realestatemanager.mappers

import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.databaseEntites.MediaItemEntity

class MediaItemToMediaItemEntityMapper {
    companion object{
        fun map(propertyId: String, mediaItem: MediaItem): MediaItemEntity{
            return MediaItemEntity(
                mediaId = mediaItem.id,
                propertyId = propertyId,
                url = mediaItem.url,
                description = mediaItem.description,
                fileType = mediaItem.fileType
            )
        }
    }
}