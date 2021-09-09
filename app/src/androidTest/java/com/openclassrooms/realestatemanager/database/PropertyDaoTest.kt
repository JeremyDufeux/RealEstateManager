package com.openclassrooms.realestatemanager.database

import androidx.test.ext.junit.runners.AndroidJUnit4
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
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
    fun testGetPropertiesAndAssertCount() {
        val propertyList = propertyDao.getProperties()

        assert(propertyList.size == 7)
    }

    @Test
    fun testGetPropertyWithIdAndAssertData() {
        val property = propertyDao.getPropertyWithId("1")

        assert(property?.propertyEntity?.propertyId == "1")
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
                    it.set(2021, 7, 25) }.timeInMillis,
                soldDate = null,
                agentName = "John McCain",
                mapPictureUrl = getStaticMapUrl(40.7589408497391, -73.97983110154246),
                dataState = DataState.NONE)

            propertyDao.insertProperty(propertyToAdd)

            val propertyList = propertyDao.getProperties()

            assert(propertyList.size == 8)
            assert(propertyList.find{ it.propertyEntity.propertyId == "101" } != null)
        }
    }

    @Test
    fun testGetPropertiesFlowAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            propertyDao.getPropertyWithIdFlow("1").collect {
                assert(it.propertyEntity.propertyId == "1")
                assert(it.propertyEntity.propertyId != "2")
                cancel()
            }
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

            assert(propertyList.size == 1)
            assert(propertyList.find{ it.propertyEntity.propertyId == "101" } != null)
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

            assert(mediaList.size == 1)
            assert(mediaList.find{ it.mediaId == mediasToAdd.mediaId} != null)
        }
    }

    @Test
    fun testAddPoiAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val poiToAdd = PointOfInterestEntity("TEST")

            propertyDao.insertPointOfInterest(poiToAdd)

            val poiList = propertyDao.getPointsOfInterest()

            assert(poiList.contains(poiToAdd))
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

            assert(!property.pointOfInterestList.contains(pointOfInterestEntity))

            val poiToAdd = PropertyPointOfInterestCrossRef(
                property.propertyEntity.propertyId,
                poi.toString(),
                DataState.WAITING_UPLOAD)

            propertyDao.insertPropertyPointOfInterestCrossRef(poiToAdd)

            property = propertyDao.getPropertyWithId(propertyId)

            assert(property?.pointOfInterestList?.size == (poiCount+1))
            assert(property?.pointOfInterestList?.contains(pointOfInterestEntity)!!)
        }
    }

    @Test
    fun testDeletePoiToPropertyAndAssertData(){
        val propertyId = "1"
        var property = propertyDao.getPropertyWithId(propertyId)!!

        assert(property.pointOfInterestList.isNotEmpty())

        propertyDao.deletePointsOfInterestForProperty(property.propertyEntity.propertyId)

        property = propertyDao.getPropertyWithId(propertyId)!!

        assert(property.pointOfInterestList.isEmpty())
    }

    @Test
    fun testDeleteMediaToPropertyAndAssertData(){
        val propertyId = "1"
        var property = propertyDao.getPropertyWithId(propertyId)!!

        assert(property.mediaList.isNotEmpty())
        val mediaToDelete = property.mediaList[0]

        propertyDao.deleteMediaWithId(mediaToDelete.mediaId)

        property = propertyDao.getPropertyWithId(propertyId)!!

        assert(!property.mediaList.contains(mediaToDelete))
    }

    @Test
    fun testUpdatePropertiesToOldAndDeleteAndAssertData(){
        propertyDao.updatePropertiesToOld()
        propertyDao.deleteOldProperties()
        val properties = propertyDao.getProperties()

        assert(properties.isEmpty())
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

            assert(properties.size == 1)
            assert(properties.find{ it.propertyEntity.propertyId == propertyId } != null)
        }
    }

    @Test
    fun testUpdateMediaToOldAndDeleteAndAssertData(){
        propertyDao.updateMediasToOld()
        propertyDao.deleteOldMedias()
        val medias = propertyDao.getMedias()

        assert(medias.isEmpty())
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

            assert(medias.size == 1)
            assert(medias.find{ it.mediaId == media.mediaId } != null)
        }
    }

    @Test
    fun testUpdatePoiToOldAndDeleteAndAssertData(){
        propertyDao.updatePointsOfInterestToOld()
        propertyDao.deleteOldPointsOfInterest()
        val poiList = propertyDao.getPointsOfInterestCrossRef()

        assert(poiList.isEmpty())
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

            assert(poiCrossRefList.size == poiList.size)
        }
    }
}