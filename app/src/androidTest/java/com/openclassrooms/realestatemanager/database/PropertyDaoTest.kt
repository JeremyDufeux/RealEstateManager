package com.openclassrooms.realestatemanager.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.models.databaseEntites.MediaItemEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PointOfInterestEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyEntity
import com.openclassrooms.realestatemanager.models.databaseEntites.PropertyPointOfInterestCrossRef
import com.openclassrooms.realestatemanager.models.enums.DataState
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import com.openclassrooms.realestatemanager.utils.getStaticMapUrl
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PropertyDaoTest{

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var propertyDao: PropertyDao


    @Before
    fun inject(){
        hiltRule.inject()
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun testGetPropertiesAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyList = propertyDao.getProperties()

            assertThat(propertyList).hasSize(7)
        }
    }

    @Test
    fun testGetPropertyWithIdAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val property = propertyDao.getPropertyWithId("1")

            assertThat(property?.propertyEntity?.propertyId).isEqualTo("1")
        }
    }

    @Test
    fun testInsertPropertiesAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyToAdd = PropertyEntity(
                propertyId = "101",
                type = PropertyType.FLAT,
                price = 213000.0,
                surface = 750.0,
                roomsAmount = 5,
                bathroomsAmount = 1,
                bedroomsAmount = 1,
                description = "Description test",
                addressLine1 = "45 Time square",
                addressLine2 = "",
                city = "Manhattan",
                postalCode = "NY 12111",
                country = "USA",
                latitude = 40.7589408497381,
                longitude = -73.97983110154346,
                postDate = Calendar.getInstance().also {
                    it.timeInMillis = 0
                    it.set(2021, 7, 25)
                }.timeInMillis,
                soldDate = null,
                agentName = "John McCain",
                mapPictureUrl = getStaticMapUrl(40.7589408497391, -73.97983110154246),
                dataState = DataState.NONE
            )

            propertyDao.insertProperty(propertyToAdd)

            val propertyList = propertyDao.getProperties()

            assertThat(propertyList).hasSize(8)
            assertThat(propertyList.find { it.propertyEntity.propertyId == "101" }).isNotNull()
        }
    }

    @Test
    fun testGetPropertiesFlowAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val property = propertyDao.getPropertyWithIdFlow("1").first()

            assertThat(property.propertyEntity.propertyId).isEqualTo("1")
            assertThat(property.propertyEntity.propertyId).isNotEqualTo("2")
        }
    }

    @Test
    fun testGetPropertiesToUploadAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyToAdd = PropertyEntity(
                propertyId = "101",
                type = PropertyType.FLAT,
                price = 213000.0,
                surface = 750.0,
                roomsAmount = 5,
                bathroomsAmount = 1,
                bedroomsAmount = 1,
                description = "Description test",
                addressLine1 = "45 Time square",
                addressLine2 = "",
                city = "Manhattan",
                postalCode = "NY 12111",
                country = "USA",
                latitude = 40.7589408497381,
                longitude = -73.97983110154346,
                postDate = Calendar.getInstance().also {
                    it.timeInMillis = 0
                    it.set(2021, 7, 25) }.timeInMillis,
                soldDate = null,
                agentName = "John McCain",
                mapPictureUrl = getStaticMapUrl(40.7589408497391, -73.97983110154246),
                dataState = DataState.WAITING_UPLOAD)

            propertyDao.insertProperty(propertyToAdd)

            val propertyList = propertyDao.getPropertiesToUpload()

            assertThat(propertyList).hasSize(1)
            assertThat(propertyList.find{ it.propertyEntity.propertyId == "101" }).isNotNull()
        }
    }

    @Test
    fun testInsertAndGetMediasToUploadAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val mediasToAdd = MediaItemEntity("Test",
                "1",
                "url",
                "description",
                FileType.PICTURE,
                DataState.WAITING_UPLOAD)

            propertyDao.insertMediaItem(mediasToAdd)

            val mediaList = propertyDao.getMediaItemsToUpload()

            assertThat(mediaList).hasSize(1)
            assertThat(mediaList.find{ it.mediaId == mediasToAdd.mediaId}).isNotNull()
        }
    }

    @Test
    fun testAddPoiAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val poiToAdd = PointOfInterestEntity("TEST")

            propertyDao.insertPointOfInterest(poiToAdd)

            val poiList = propertyDao.getPointsOfInterest()

            assertThat(poiList).contains(poiToAdd)
        }
    }

    @Test
    fun testAddPoiToPropertyAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyId = "1"
            var property = propertyDao.getPropertyWithId(propertyId)
            val poi = PointOfInterest.GROCERY
            val pointOfInterestEntity = PointOfInterestEntity(poi.toString())
            val poiCount = property?.pointOfInterestList?.size!!

            assertThat(property.pointOfInterestList).doesNotContain(pointOfInterestEntity)

            val poiToAdd = PropertyPointOfInterestCrossRef(
                property.propertyEntity.propertyId,
                poi.toString(),
                DataState.WAITING_UPLOAD)

            propertyDao.insertPropertyPointOfInterestCrossRef(poiToAdd)

            property = propertyDao.getPropertyWithId(propertyId)

            assertThat(property?.pointOfInterestList).hasSize(poiCount+1)
            assertThat(property?.pointOfInterestList).contains(pointOfInterestEntity)
        }
    }

    @Test
    fun testDeletePoiToPropertyAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyId = "1"
            var property = propertyDao.getPropertyWithId(propertyId)!!

            assertThat(property.pointOfInterestList).isNotEmpty()

            propertyDao.deletePointsOfInterestForProperty(property.propertyEntity.propertyId)

            property = propertyDao.getPropertyWithId(propertyId)!!

            assertThat(property.pointOfInterestList).isEmpty()
        }
    }

    @Test
    fun testDeleteMediaToPropertyAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyId = "1"
            var property = propertyDao.getPropertyWithId(propertyId)!!

            assertThat(property.mediaList).isNotEmpty()
            val mediaToDelete = property.mediaList[0]

            propertyDao.deleteMediaWithId(mediaToDelete.mediaId)

            property = propertyDao.getPropertyWithId(propertyId)!!

            assertThat(property.mediaList).doesNotContain(mediaToDelete)
        }
    }

    @Test
    fun testUpdatePropertiesToOldAndDeleteAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            propertyDao.updatePropertiesToOld()
            propertyDao.deleteOldProperties()
            val properties = propertyDao.getProperties()

            assertThat(properties).isEmpty()
        }
    }

    @Test
    fun testUpdatePropertiesToOldAndUpdateAndDeleteAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyId = "1"
            val property = propertyDao.getPropertyWithId(propertyId)!!

            propertyDao.updatePropertiesToOld()

            property.propertyEntity.dataState = DataState.SERVER

            propertyDao.insertProperty(property.propertyEntity)

            propertyDao.deleteOldProperties()

            val properties = propertyDao.getProperties()

            assertThat(properties).hasSize(1)
            assertThat(properties.find{ it.propertyEntity.propertyId == propertyId }).isNotNull()
        }
    }

    @Test
    fun testUpdateMediaToOldAndDeleteAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            propertyDao.updateMediasToOld()
            propertyDao.deleteOldMedias()
            val medias = propertyDao.getMedias()

            assertThat(medias).isEmpty()
        }
    }

    @Test
    fun testUpdateMediaToOldAndUpdateAndDeleteAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyId = "1"
            val media = propertyDao.getPropertyWithId(propertyId)?.mediaList?.get(0)!!

            propertyDao.updateMediasToOld()

            media.dataState = DataState.SERVER

            propertyDao.insertMediaItem(media)

            propertyDao.deleteOldMedias()

            val medias = propertyDao.getMedias()

            assertThat(medias).hasSize(1)
            assertThat(medias.find{ it.mediaId == media.mediaId }).isNotNull()
        }
    }

    @Test
    fun testUpdatePoiToOldAndDeleteAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            propertyDao.updatePointsOfInterestToOld()
            propertyDao.deleteOldPointsOfInterest()
            val poiList = propertyDao.getPointsOfInterestCrossRef()

            assertThat(poiList).isEmpty()
        }
    }

    @Test
    fun testUpdatePoiToOldAndUpdateAndDeleteAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyId = "1"
            val poiList = propertyDao.getPropertyWithId(propertyId)?.pointOfInterestList!!

            propertyDao.updatePointsOfInterestToOld()

            for(poi in poiList) {
                val poiCrossRef = PropertyPointOfInterestCrossRef(propertyId, poi.description, DataState.SERVER)
                propertyDao.insertPropertyPointOfInterestCrossRef(poiCrossRef)
            }

            propertyDao.deleteOldPointsOfInterest()

            val poiCrossRefList = propertyDao.getPointsOfInterestCrossRef()

            assertThat(poiCrossRefList.size).isEqualTo(poiList.size)
        }
    }
}