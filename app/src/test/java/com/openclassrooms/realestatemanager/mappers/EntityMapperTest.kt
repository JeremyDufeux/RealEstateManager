package com.openclassrooms.realestatemanager.mappers

import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.databaseEntites.MediaItemEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PointOfInterestEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyWithMediaItemAndPointsOfInterestEntity
import com.openclassrooms.realestatemanager.models.enums.DataState
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import com.openclassrooms.realestatemanager.utils.getStaticMapUrl
import junit.framework.TestCase
import org.junit.Test
import java.util.*

class EntityMapperTest: TestCase() {

    @Test
    fun testPropertyToPropertyEntityMapper() {
        val property = Property(
            id = "101",
            type = PropertyType.FLAT,
            price = 295000.0,
            surface = 800.0,
            roomsAmount = 3,
            bathroomsAmount = 1,
            bedroomsAmount = 1,
            description = "This 800 square foot condo home has 2 bedrooms and 1.0 bathrooms. This home is located at 323 Edgecombe Ave APT 7).isEqualTo(New York).isEqualTo(NY 10031",
            addressLine1 = "323 Edgecombe Ave APT 7",
            city = "Harlem",
            postalCode = "NY 10031",
            country = "USA",
            latitude = 40.82568643585645,
            longitude = -73.94261050737286,
            postDate = Calendar.getInstance().also {
                it.timeInMillis = 0
                it.set(2021, 3, 10) }.timeInMillis,
            soldDate = Calendar.getInstance().also {
                it.timeInMillis = 0
                it.set(2021, 8, 8) }.timeInMillis,
            agentName = "Kristen Fortino",
            mapPictureUrl = getStaticMapUrl(40.82568643585645, -73.94261050737286)
        )

        val propertyEntity = propertyToPropertyEntityMapper(property, DataState.NONE)

        assertThat(property.id).isEqualTo(propertyEntity.propertyId)
        assertThat(property.type).isEqualTo(propertyEntity.type)
        assertThat(property.price).isEqualTo(propertyEntity.price)
        assertThat(property.surface).isEqualTo(propertyEntity.surface)
        assertThat(property.roomsAmount).isEqualTo(propertyEntity.roomsAmount)
        assertThat(property.bathroomsAmount).isEqualTo(propertyEntity.bathroomsAmount)
        assertThat(property.description).isEqualTo(propertyEntity.description)
        assertThat(property.addressLine1).isEqualTo(propertyEntity.addressLine1)
        assertThat(property.addressLine2).isEqualTo(propertyEntity.addressLine2)
        assertThat(property.city).isEqualTo(propertyEntity.city)
        assertThat(property.postalCode).isEqualTo(propertyEntity.postalCode)
        assertThat(property.country).isEqualTo(propertyEntity.country)
        assertThat(property.latitude).isEqualTo(propertyEntity.latitude)
        assertThat(property.longitude).isEqualTo(propertyEntity.longitude)
        assertThat(property.postDate).isEqualTo(propertyEntity.postDate)
        assertThat(property.soldDate).isEqualTo(propertyEntity.soldDate)
        assertThat(property.agentName).isEqualTo(propertyEntity.agentName)
        assertThat(DataState.NONE).isEqualTo(propertyEntity.dataState)
    }

    @Test
    fun testPropertyToPropertyEntityMapperWithEmptyValues() {
        val property = Property()

        val propertyEntity = propertyToPropertyEntityMapper(property, DataState.NONE)

        assertThat(propertyEntity.propertyId).isEqualTo("")
        assertThat(propertyEntity.type).isEqualTo(PropertyType.FLAT)
        assertThat(propertyEntity.price).isEqualTo(null)
        assertThat(propertyEntity.surface).isEqualTo(null)
        assertThat(propertyEntity.roomsAmount).isEqualTo(null)
        assertThat(propertyEntity.bathroomsAmount).isEqualTo(null)
        assertThat(propertyEntity.description).isEqualTo("")
        assertThat(propertyEntity.addressLine1).isEqualTo("")
        assertThat(propertyEntity.addressLine2).isEqualTo("")
        assertThat(propertyEntity.city).isEqualTo("")
        assertThat(propertyEntity.postalCode).isEqualTo("")
        assertThat(propertyEntity.country).isEqualTo("")
        assertThat(propertyEntity.latitude).isEqualTo(null)
        assertThat(propertyEntity.longitude).isEqualTo(null)
        assertThat(propertyEntity.postDate).isEqualTo(0)
        assertThat(propertyEntity.soldDate).isEqualTo(null)
        assertThat(propertyEntity.agentName).isEqualTo("")
        assertThat(propertyEntity.dataState).isEqualTo(DataState.NONE)
    }


    @Test
    fun testPointsOfInterestEntityToPointsOfInterestMapper() {
        val pointsOfInterestEntityList = listOf(
            PointOfInterestEntity("SCHOOL"),
            PointOfInterestEntity("GROCERY"),
            PointOfInterestEntity("FITNESS_CLUB")
        )

        val pointsOfInterestList = pointsOfInterestEntityToPointsOfInterestMapper(pointsOfInterestEntityList)

        assertThat(pointsOfInterestList).hasSize(pointsOfInterestEntityList.size)
        assertThat(pointsOfInterestList.firstOrNull { it == PointOfInterest.GROCERY }).isNotNull()
        assertThat(pointsOfInterestList.firstOrNull { it == PointOfInterest.PARKING }).isNull()
    }

    @Test
    fun testMediaItemToMediaItemEntityMapper(){
        val mediaList = listOf(
            MediaItem("16fe027f-2e99-401f-ac3b-d2462d0083d7",
                "6",
                "https://photos.zillowstatic.com/fp/8e335a55b050bf45a3d2777fd1060659-cc_ft_768.jpg" ,
                "Dining room",
                FileType.PICTURE),
            MediaItem("2c4f0a24-6da4-481e-ad83-f769812e8eaa",
                "5",
                "https://photos.zillowstatic.com/fp/ecec97465481bdde4c0b095fd6ae7119-cc_ft_384.jpg" ,
                "Facade 2",
                FileType.PICTURE),
        )

        val mediaEntityList = mutableListOf<MediaItemEntity>()
        for(mediaItem in mediaList) {
            mediaEntityList.add(mediaItemToMediaItemEntityMapper(mediaItem))
        }

        assertThat(mediaEntityList).hasSize(mediaList.size)
        assertThat(mediaEntityList.firstOrNull { it.mediaId == mediaList[0].id }).isNotNull()
        assertThat(mediaEntityList.firstOrNull { it.description == mediaList[0].description }).isNotNull()
        assertThat(mediaEntityList.firstOrNull { it.fileType == mediaList[0].fileType }).isNotNull()
        assertThat(mediaEntityList.firstOrNull { it.propertyId == mediaList[0].propertyId }).isNotNull()
        assertThat(mediaEntityList.firstOrNull { it.url == mediaList[0].url }).isNotNull()
    }

    @Test
    fun testMediaItemsEntityToMediaItemsMapper(){
        val mediaEntityList = listOf(
            MediaItemEntity("16fe027f-2e99-401f-ac3b-d2462d0083d7",
                "6",
                "https://photos.zillowstatic.com/fp/8e335a55b050bf45a3d2777fd1060659-cc_ft_768.jpg" ,
                "Dining room",
                FileType.PICTURE,
                DataState.NONE),
            MediaItemEntity("2c4f0a24-6da4-481e-ad83-f769812e8eaa",
                "5",
                "https://photos.zillowstatic.com/fp/ecec97465481bdde4c0b095fd6ae7119-cc_ft_384.jpg" ,
                "Facade 2",
                FileType.PICTURE,
                DataState.NONE),
        )

        val mediaList = mediaItemsEntityToMediaItemsMapper(mediaEntityList)

        assertThat(mediaEntityList).hasSize(mediaList.size)
        assertThat(mediaEntityList.firstOrNull { it.mediaId == mediaList[0].id }).isNotNull()
        assertThat(mediaEntityList.firstOrNull { it.description == mediaList[0].description }).isNotNull()
        assertThat(mediaEntityList.firstOrNull { it.fileType == mediaList[0].fileType }).isNotNull()
        assertThat(mediaEntityList.firstOrNull { it.propertyId == mediaList[0].propertyId }).isNotNull()
        assertThat(mediaEntityList.firstOrNull { it.url == mediaList[0].url }).isNotNull()

    }

    @Test
    fun testPropertyEntityToPropertyMapper() {
        val propertyEntity = PropertyEntity(
            propertyId = "101",
            type = PropertyType.FLAT,
            price = 295000.0,
            surface = 800.0,
            roomsAmount = 3,
            bathroomsAmount = 1,
            bedroomsAmount = 1,
            description = "This 800 square foot condo home has 2 bedrooms and 1.0 bathrooms. This home is located at 323 Edgecombe Ave APT 7).isEqualTo(New York).isEqualTo(NY 10031",
            addressLine1 = "323 Edgecombe Ave APT 7",
            addressLine2 = "",
            city = "Harlem",
            postalCode = "NY 10031",
            country = "USA",
            latitude = 40.82568643585645,
            longitude = -73.94261050737286,
            postDate = Calendar.getInstance().also {
                it.timeInMillis = 0
                it.set(2021, 3, 10) }.timeInMillis,
            soldDate = Calendar.getInstance().also {
                it.timeInMillis = 0
                it.set(2021, 8, 8) }.timeInMillis,
            agentName = "Kristen Fortino",
            mapPictureUrl = getStaticMapUrl(40.82568643585645, -73.94261050737286),
            dataState = DataState.NONE
        )

        val mediaEntityList = listOf(
            MediaItemEntity("16fe027f-2e99-401f-ac3b-d2462d0083d7",
                "6",
                "https://photos.zillowstatic.com/fp/8e335a55b050bf45a3d2777fd1060659-cc_ft_768.jpg" ,
                "Dining room",
                FileType.PICTURE,
                DataState.NONE),
            MediaItemEntity("2c4f0a24-6da4-481e-ad83-f769812e8eaa",
                "5",
                "https://photos.zillowstatic.com/fp/ecec97465481bdde4c0b095fd6ae7119-cc_ft_384.jpg" ,
                "Facade 2",
                FileType.PICTURE,
                DataState.NONE),
        )

        val pointsOfInterestEntityList = listOf(
            PointOfInterestEntity("SCHOOL"),
            PointOfInterestEntity("GROCERY"),
            PointOfInterestEntity("PARK")
        )

        val propertyWithMediaItemAndPointsOfInterestEntity = PropertyWithMediaItemAndPointsOfInterestEntity(
            propertyEntity,
            mediaEntityList,
            pointsOfInterestEntityList)

        val property = propertyEntityToPropertyMapper(propertyWithMediaItemAndPointsOfInterestEntity)

        assertThat(property.id).isEqualTo(propertyEntity.propertyId)
        assertThat(property.type).isEqualTo(propertyEntity.type)
        assertThat(property.price).isEqualTo(propertyEntity.price)
        assertThat(property.surface).isEqualTo(propertyEntity.surface)
        assertThat(property.roomsAmount).isEqualTo(propertyEntity.roomsAmount)
        assertThat(property.bathroomsAmount).isEqualTo(propertyEntity.bathroomsAmount)
        assertThat(property.bedroomsAmount).isEqualTo(propertyEntity.bedroomsAmount)
        assertThat(property.description).isEqualTo(propertyEntity.description)
        assertThat(property.addressLine1).isEqualTo(propertyEntity.addressLine1)
        assertThat(property.addressLine2).isEqualTo(propertyEntity.addressLine2)
        assertThat(property.city).isEqualTo(propertyEntity.city)
        assertThat(property.postalCode).isEqualTo(propertyEntity.postalCode)
        assertThat(property.country).isEqualTo(propertyEntity.country)
        assertThat(property.latitude).isEqualTo(propertyEntity.latitude)
        assertThat(property.longitude).isEqualTo(propertyEntity.longitude)
        assertThat(property.postDate).isEqualTo(propertyEntity.postDate)
        assertThat(property.soldDate).isEqualTo(propertyEntity.soldDate)
        assertThat(property.agentName).isEqualTo(propertyEntity.agentName)
        assertThat(property.mapPictureUrl).isEqualTo(propertyEntity.mapPictureUrl)

        assertThat(property.mediaList).hasSize(mediaEntityList.size)

        assertThat(property.mediaList.firstOrNull { it.id == mediaEntityList[0].mediaId }).isNotNull()
        assertThat(property.mediaList.firstOrNull { it.description == mediaEntityList[0].description }).isNotNull()
        assertThat(property.mediaList.firstOrNull { it.fileType == mediaEntityList[0].fileType }).isNotNull()
        assertThat(property.mediaList.firstOrNull { it.propertyId == mediaEntityList[0].propertyId }).isNotNull()
        assertThat(property.mediaList.firstOrNull { it.url == mediaEntityList[0].url }).isNotNull()

        assertThat(property.pointOfInterestList).hasSize(pointsOfInterestEntityList.size)
        assertThat(property.pointOfInterestList.firstOrNull { it == PointOfInterest.GROCERY }).isNotNull()
        assertThat(property.pointOfInterestList.firstOrNull { it == PointOfInterest.PARKING }).isNull()
    }
}