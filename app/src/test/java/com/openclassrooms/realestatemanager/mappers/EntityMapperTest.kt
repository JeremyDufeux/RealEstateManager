package com.openclassrooms.realestatemanager.mappers

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
            description = "This 800 square foot condo home has 2 bedrooms and 1.0 bathrooms. This home is located at 323 Edgecombe Ave APT 7, New York, NY 10031",
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

        assertEquals(property.id, propertyEntity.propertyId)
        assertEquals(property.type, propertyEntity.type)
        assertEquals(property.price, propertyEntity.price)
        assertEquals(property.surface, propertyEntity.surface)
        assertEquals(property.roomsAmount, propertyEntity.roomsAmount)
        assertEquals(property.bathroomsAmount, propertyEntity.bathroomsAmount)
        assertEquals(property.description, propertyEntity.description)
        assertEquals(property.addressLine1, propertyEntity.addressLine1)
        assertEquals(property.addressLine2, propertyEntity.addressLine2)
        assertEquals(property.city, propertyEntity.city)
        assertEquals(property.postalCode, propertyEntity.postalCode)
        assertEquals(property.country, propertyEntity.country)
        assertEquals(property.latitude, propertyEntity.latitude)
        assertEquals(property.longitude, propertyEntity.longitude)
        assertEquals(property.postDate, propertyEntity.postDate)
        assertEquals(property.soldDate, propertyEntity.soldDate)
        assertEquals(property.agentName, propertyEntity.agentName)
        assertEquals(DataState.NONE, propertyEntity.dataState)
    }

    @Test
    fun testPropertyToPropertyEntityMapperWithEmptyValues() {
        val property = Property()

        val propertyEntity = propertyToPropertyEntityMapper(property, DataState.NONE)

        assertEquals("", propertyEntity.propertyId)
        assertEquals(PropertyType.FLAT, propertyEntity.type)
        assertEquals(null, propertyEntity.price)
        assertEquals(null, propertyEntity.surface)
        assertEquals(null, propertyEntity.roomsAmount)
        assertEquals(null, propertyEntity.bathroomsAmount)
        assertEquals("", propertyEntity.description)
        assertEquals("", propertyEntity.addressLine1)
        assertEquals("", propertyEntity.addressLine2)
        assertEquals("", propertyEntity.city)
        assertEquals("", propertyEntity.postalCode)
        assertEquals("", propertyEntity.country)
        assertEquals(null, propertyEntity.latitude)
        assertEquals(null, propertyEntity.longitude)
        assertEquals(0, propertyEntity.postDate)
        assertEquals(null, propertyEntity.soldDate)
        assertEquals("", propertyEntity.agentName)
        assertEquals(DataState.NONE, propertyEntity.dataState)
    }


    @Test
    fun testPointsOfInterestEntityToPointsOfInterestMapper() {
        val pointsOfInterestEntityList = listOf(
            PointOfInterestEntity("SCHOOL"),
            PointOfInterestEntity("GROCERY"),
            PointOfInterestEntity("FITNESS_CLUB")
        )

        val pointsOfInterestList = pointsOfInterestEntityToPointsOfInterestMapper(pointsOfInterestEntityList)

        assertEquals(pointsOfInterestEntityList.size, pointsOfInterestList.size)
        assertEquals(true, pointsOfInterestList.firstOrNull { it == PointOfInterest.GROCERY } != null)
        assertEquals(true, pointsOfInterestList.firstOrNull { it == PointOfInterest.PARKING } == null)
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

        assertEquals(mediaList.size, mediaEntityList.size)
        assertEquals(true, mediaEntityList.firstOrNull { it.mediaId == mediaList[0].id } != null)
        assertEquals(true, mediaEntityList.firstOrNull { it.description == mediaList[0].description } != null)
        assertEquals(true, mediaEntityList.firstOrNull { it.fileType == mediaList[0].fileType } != null)
        assertEquals(true, mediaEntityList.firstOrNull { it.propertyId == mediaList[0].propertyId } != null)
        assertEquals(true, mediaEntityList.firstOrNull { it.url == mediaList[0].url } != null)
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

        assertEquals(mediaEntityList.size, mediaList.size)
        assertEquals(true, mediaList.firstOrNull { it.id == mediaEntityList[0].mediaId } != null)
        assertEquals(true, mediaList.firstOrNull { it.description == mediaEntityList[0].description } != null)
        assertEquals(true, mediaList.firstOrNull { it.fileType == mediaEntityList[0].fileType } != null)
        assertEquals(true, mediaList.firstOrNull { it.propertyId == mediaEntityList[0].propertyId } != null)
        assertEquals(true, mediaList.firstOrNull { it.url == mediaEntityList[0].url } != null)

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
            description = "This 800 square foot condo home has 2 bedrooms and 1.0 bathrooms. This home is located at 323 Edgecombe Ave APT 7, New York, NY 10031",
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

        assertEquals(propertyEntity.propertyId, property.id)
        assertEquals(propertyEntity.type, property.type)
        assertEquals(propertyEntity.price, property.price)
        assertEquals(propertyEntity.surface, property.surface)
        assertEquals(propertyEntity.roomsAmount, property.roomsAmount)
        assertEquals(propertyEntity.bathroomsAmount, property.bathroomsAmount)
        assertEquals(propertyEntity.bedroomsAmount, property.bedroomsAmount)
        assertEquals(propertyEntity.description, property.description)
        assertEquals(propertyEntity.addressLine1, property.addressLine1)
        assertEquals(propertyEntity.addressLine2, property.addressLine2)
        assertEquals(propertyEntity.city, property.city)
        assertEquals(propertyEntity.postalCode, property.postalCode)
        assertEquals(propertyEntity.country, property.country)
        assertEquals(propertyEntity.latitude, property.latitude)
        assertEquals(propertyEntity.longitude, property.longitude)
        assertEquals(propertyEntity.postDate, property.postDate)
        assertEquals(propertyEntity.soldDate, property.soldDate)
        assertEquals(propertyEntity.agentName, property.agentName)
        assertEquals(propertyEntity.mapPictureUrl, property.mapPictureUrl)

        assertEquals(mediaEntityList.size, property.mediaList.size)
        assertEquals(true, property.mediaList.firstOrNull { it.id == mediaEntityList[0].mediaId } != null)
        assertEquals(true, property.mediaList.firstOrNull { it.description == mediaEntityList[0].description } != null)
        assertEquals(true, property.mediaList.firstOrNull { it.fileType == mediaEntityList[0].fileType } != null)
        assertEquals(true, property.mediaList.firstOrNull { it.propertyId == mediaEntityList[0].propertyId } != null)
        assertEquals(true, property.mediaList.firstOrNull { it.url == mediaEntityList[0].url } != null)

        assertEquals(pointsOfInterestEntityList.size, property.pointOfInterestList.size)
        assertEquals(true, property.pointOfInterestList.firstOrNull { it == PointOfInterest.GROCERY } != null)
        assertEquals(true, property.pointOfInterestList.firstOrNull { it == PointOfInterest.PARKING } == null)

    }

}