package com.openclassrooms.realestatemanager.repositories

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.PropertyFilter
import com.openclassrooms.realestatemanager.models.enums.DataState
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import com.openclassrooms.realestatemanager.utils.generateOfflineProperties
import com.openclassrooms.realestatemanager.utils.getStaticMapUrl
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
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
class OfflinePropertyRepositoryTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var mOfflinePropertyRepository: OfflinePropertyRepository


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
    fun testGetProperties(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyList = mOfflinePropertyRepository.getProperties()

            assertEquals(7, propertyList.size)
        }
    }

    @Test
    fun testGetPropertyWithIdAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main){
            val property = mOfflinePropertyRepository.getPropertyWithId("1")

            assertEquals("1", property?.id)
            assertEquals(169000.0, property?.price)
        }
    }

    @Test
    fun testGetPropertyWithIdFlowAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main){
            val property = mOfflinePropertyRepository.getPropertyWithIdFlow("1").first()

            assertEquals("1", property.id)
            assertEquals(169000.0, property.price)
        }
    }

    @Test
    fun testUpdateDatabaseWithTwoPropertyAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val newPropertyList = generateOfflineProperties().subList(0, 2)

            mOfflinePropertyRepository.updateDatabase(newPropertyList)

            val properties = mOfflinePropertyRepository.getProperties()

            assertEquals(newPropertyList.size, properties.size)
            assertEquals(true, properties.firstOrNull { it.id == newPropertyList[0].id } != null)
            assertEquals(true, properties.firstOrNull { it.id == "5" } == null)
        }
    }

    @Test
    fun testAddPropertyAndGetPropertyToUploadAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertiesCount = mOfflinePropertyRepository.getProperties().size

            val id = "101"
            val propertyToAdd = Property(
                id = id,
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

            mOfflinePropertyRepository.addProperty(propertyToAdd, DataState.WAITING_UPLOAD)

            val properties = mOfflinePropertyRepository.getProperties()
            val propertyToUpload = mOfflinePropertyRepository.getPropertiesToUpload()

            assertEquals(propertiesCount + 1 , properties.size)
            assertEquals(true, propertyToUpload.firstOrNull { it.id == id } != null)
        }
    }

    @Test
    fun testAddMediaToUploadAndGetItAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val mediaToAdd = MediaItem("16fe027f-2e99-401f-ac3b-d2462d0083d7",
                "6",
                "https://photos.zillowstatic.com/fp/8e335a55b050bf45a3d2777fd1060659-cc_ft_768.jpg" ,
                "Dining room",
                FileType.PICTURE)

            mOfflinePropertyRepository.addMediaToUpload(mediaToAdd)

            val mediasToUpload = mOfflinePropertyRepository.getMediaItemsToUpload()

            assertEquals(true, mediasToUpload.firstOrNull { it.id == mediaToAdd.id } != null)
        }
    }

    @Test
    fun testDeleteMediaAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val property = mOfflinePropertyRepository.getPropertyWithId("1")!!

            mOfflinePropertyRepository.deleteMedia(property.mediaList[0])

            val propertyAfterDelete = mOfflinePropertyRepository.getPropertyWithId("1")!!

            assertEquals(property.mediaList.size - 1, propertyAfterDelete.mediaList.size)
        }
    }

    @Test
    fun testSetMediatToDeleteAndGetItAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val property = mOfflinePropertyRepository.getPropertyWithId("1")!!
            val mediaItem = property.mediaList[0]

            mOfflinePropertyRepository.setMediaToDelete(mediaItem)

            val mediasToDelete = mOfflinePropertyRepository.getMediaItemsToDelete()

            assertEquals(true, mediasToDelete.firstOrNull{it.id == mediaItem.id} != null)
        }
    }

    @Test
    fun testUpdatePropertyAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val property = mOfflinePropertyRepository.getPropertyWithId("1")!!
            property.price = property.price?.times(2)

            mOfflinePropertyRepository.updateProperty(property)

            val propertyAfterUpdate = mOfflinePropertyRepository.getPropertyWithId("1")!!

            assertEquals(property.price, propertyAfterUpdate.price)
        }
    }

    @Test
    fun testGetPropertyWithFiltersAndAssertData(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyTypeList = mutableListOf(PropertyType.FLAT, PropertyType.LAND, PropertyType.TRIPLEX)
            val pointOfInterest = mutableListOf(PointOfInterest.PUBLIC_TRANSPORT, PointOfInterest.SCHOOL, PointOfInterest.PARKING)
            val propertyFilter = PropertyFilter(
                minPrice = 200000,
                maxPrice = 300000,
                minSurface = 700,
                maxSurface = 900,
                propertyTypeList = propertyTypeList,
                pointOfInterestList = pointOfInterest,
                city = "Harlem",
                roomsAmount = 2,
                bathroomsAmount = 1,
                bedroomsAmount = 1,
                mediasAmount = 3,
                sold = true,
                soldDate = 1628294400000) // 7 August 2021

            val properties = mOfflinePropertyRepository.getPropertyWithFilters(propertyFilter)

            assertEquals(1, properties.size)
        }
    }
}