package com.openclassrooms.realestatemanager.repositories

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.MockitoHelper
import com.openclassrooms.realestatemanager.api.PropertyApiService
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.utils.generateProperties
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PropertyRepositoryTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var mPropertyApiService: PropertyApiService = mock(PropertyApiService::class.java)
    private val mPropertyRepository = PropertyRepository(mPropertyApiService)

    @Before
    fun inject() {
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
    fun testFetchPropertiesAndAssertResult(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            `when`(
                mPropertyApiService.fetchProperties()
            ).thenReturn(State.Download.DownloadSuccess(generateProperties()))

            assertEquals(State.Idle, mPropertyRepository.stateFlow.first())

            mPropertyRepository.fetchProperties()
            val result = mPropertyRepository.stateFlow.first()

            assertEquals(true, result is State.Download.DownloadSuccess)
            assertEquals(
                generateProperties().size,
                (result as State.Download.DownloadSuccess).propertiesList.size
            )
        }
    }

    @Test
    fun testAddPropertiesAndAssertResult(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            `when`(
                mPropertyApiService.addProperty(MockitoHelper.anyObject())
            ).thenReturn(State.Upload.UploadSuccess.Empty)

            val propertyToAdd = Property(
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
                    it.set(2021, 3, 10)
                }.timeInMillis,
                soldDate = Calendar.getInstance().also {
                    it.timeInMillis = 0
                    it.set(2021, 8, 8)
                }.timeInMillis,
                agentName = "Kristen Fortino",
                mapPictureUrl = getStaticMapUrl(40.82568643585645, -73.94261050737286)
            )

            mPropertyRepository.addProperty(propertyToAdd)

            assertEquals(State.Upload.UploadSuccess.Empty, mPropertyRepository.stateFlow.first())
        }
    }

    @Test
    fun testAddPropertyWithMediasAndAssertResult(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            `when`(
                mPropertyApiService.addProperty(MockitoHelper.anyObject())
            ).thenReturn(State.Upload.UploadSuccess.Empty)
            `when`(
                mPropertyApiService.uploadMedia(MockitoHelper.anyObject())
            ).thenReturn(State.Upload.UploadSuccess.Url("Fake url"))

            val mediaList = listOf(
                MediaItem(
                    "16fe027f-2e99-401f-ac3b-d2462d0083d7",
                    "6",
                    "https://photos.zillowstatic.com/fp/8e335a55b050bf45a3d2777fd1060659-cc_ft_768.jpg",
                    "Dining room",
                    FileType.PICTURE
                ),
                MediaItem(
                    "2c4f0a24-6da4-481e-ad83-f769812e8eaa",
                    "5",
                    "https://photos.zillowstatic.com/fp/ecec97465481bdde4c0b095fd6ae7119-cc_ft_384.jpg",
                    "Facade 2",
                    FileType.PICTURE
                ),
            )

            val propertyToAdd = Property(
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
                    it.set(2021, 3, 10)
                }.timeInMillis,
                soldDate = Calendar.getInstance().also {
                    it.timeInMillis = 0
                    it.set(2021, 8, 8)
                }.timeInMillis,
                agentName = "Kristen Fortino",
                mapPictureUrl = getStaticMapUrl(40.82568643585645, -73.94261050737286),
                mediaList = mediaList
            )

            mPropertyRepository.addPropertyWithMedias(propertyToAdd)

            assertEquals(State.Upload.UploadSuccess.Empty, mPropertyRepository.stateFlow.first())
        }
    }

    @Test
    fun testUploadMediasAndAssertResult(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            `when`(
                mPropertyApiService.uploadMedia(MockitoHelper.anyObject())
            ).thenReturn(State.Upload.UploadSuccess.Url("Fake url"))

            val mediaToAdd = MediaItem(
                "16fe027f-2e99-401f-ac3b-d2462d0083d7",
                "6",
                "https://photos.zillowstatic.com/fp/8e335a55b050bf45a3d2777fd1060659-cc_ft_768.jpg",
                "Dining room",
                FileType.PICTURE)

            mPropertyRepository.uploadMedia(mediaToAdd)

            assertEquals(State.Idle, mPropertyRepository.stateFlow.first())

        }
    }
    @Test
    fun testDeleteMediasAndAssertResult(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            `when`(
                mPropertyApiService.deleteMedia(MockitoHelper.anyObject())
            ).thenReturn(State.Upload.UploadSuccess.Empty)

            val mediaToDelete = MediaItem(
                "16fe027f-2e99-401f-ac3b-d2462d0083d7",
                "6",
                "https://photos.zillowstatic.com/fp/8e335a55b050bf45a3d2777fd1060659-cc_ft_768.jpg",
                "Dining room",
                FileType.PICTURE)

            mPropertyRepository.deleteMedia(mediaToDelete)

            assertEquals(State.Idle, mPropertyRepository.stateFlow.first())
        }
    }
}