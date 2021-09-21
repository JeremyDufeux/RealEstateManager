package com.openclassrooms.realestatemanager.repositories

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.PropertyFilter
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.utils.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject
import kotlin.time.ExperimentalTime


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PropertyUseCaseTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var mPropertyUseCase: PropertyUseCase

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

    @ExperimentalTime
    @Test
    fun testFetchPropertiesOnline(): Unit = runBlocking {  // OK
        launch(Dispatchers.Main) {
            mPropertyUseCase.stateFlow.test {
                assertThat(awaitItem()).isInstanceOf(State.Idle::class.java)

                mPropertyUseCase.fetchProperties()
                assertThat(awaitItem()).isInstanceOf(State.Download.Downloading::class.java)

                val result = awaitItem()
                assertThat(result).isInstanceOf(State.Download.DownloadSuccess::class.java)

                val list = (result as State.Download.DownloadSuccess).propertiesList
                assertThat(list.size).isEqualTo(generateOnlineProperties().size)
            }
        }
    }

    @ExperimentalTime
    @Test
    fun testFetchPropertiesOffline(): Unit = runBlocking { // Ok
        disableConnections()
        delay(CONNECTION_DELAY)

        launch(Dispatchers.Main) {
            mPropertyUseCase.stateFlow.test {

                assertThat(awaitItem()).isInstanceOf(State.Idle::class.java)

                mPropertyUseCase.fetchProperties()
                assertThat(awaitItem()).isInstanceOf(State.Download.Error::class.java)

                val result = awaitItem()
                assertThat(result).isInstanceOf(State.OfflineSuccess::class.java)

                val list = (result as State.OfflineSuccess).propertiesList
                assertThat(list.size).isEqualTo(generateOfflineProperties().size)
            }
        }
        enableConnections()
        delay(CONNECTION_DELAY)
    }

    @ExperimentalTime
    @Test
    fun testGetOfflineProperties(): Unit = runBlocking { // Ok
        launch(Dispatchers.Main){
            mPropertyUseCase.stateFlow.test {
                assertThat(awaitItem()).isInstanceOf(State.Idle::class.java)

                mPropertyUseCase.getOfflineProperties()

                val result = awaitItem()
                assertThat(result).isInstanceOf(State.OfflineSuccess::class.java)

                val list = (result as State.OfflineSuccess).propertiesList
                assertThat(list.size).isEqualTo(generateOfflineProperties().size)
            }
        }
    }

    @Test
    fun testGetPropertyWithFlow(): Unit = runBlocking {
        launch(Dispatchers.Main){
            val property = mPropertyUseCase.getPropertyWithIdFlow("1").first()

            assertThat(property).isNotNull()
            assertThat(property.id).isEqualTo("1")
        }
    }

    @ExperimentalTime
    @Test
    fun testAddPropertyOnline(): Unit = runBlocking { // OK
        launch(Dispatchers.Main) {
            val propertyToAdd = Property(
                id = "201",
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
                mapPictureUrl = getStaticMapUrl(40.7589408497391, -73.97983110154246),)

            mPropertyUseCase.stateFlow.test {
                assertThat(awaitItem()).isInstanceOf(State.Idle::class.java)

                mPropertyUseCase.addProperty(propertyToAdd)
                assertThat(awaitItem()).isInstanceOf(State.Upload.Uploading::class.java)

                assertThat(awaitItem()).isInstanceOf(State.Upload.UploadSuccess.Empty::class.java)

                mPropertyUseCase.fetchProperties()

                val result = awaitItem()
                assertThat(result).isInstanceOf(State.Download.DownloadSuccess::class.java)

                val list = (result as State.Download.DownloadSuccess).propertiesList
                assertThat(list.size).isEqualTo(generateOnlineProperties().size + 1)
            }
        }
    }

    @ExperimentalTime
    @Test
    fun testAddPropertyOffline(): Unit = runBlocking { // Ok
        disableConnections()
        delay(CONNECTION_DELAY)
        launch(Dispatchers.Main) {
            val propertyToAdd = Property(
                id = "201",
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
                mapPictureUrl = getStaticMapUrl(40.7589408497391, -73.97983110154246),)

            mPropertyUseCase.stateFlow.test {
                assertThat(awaitItem()).isInstanceOf(State.Idle::class.java)

                mPropertyUseCase.addProperty(propertyToAdd)
                assertThat(awaitItem()).isInstanceOf(State.OfflineSuccess::class.java)

                assertThat(awaitItem()).isInstanceOf(State.Upload.Error::class.java)

                mPropertyUseCase.fetchProperties()
                assertThat(awaitItem()).isInstanceOf(State.Download.Error::class.java)

                val result = awaitItem()

                val list = (result as State.OfflineSuccess).propertiesList
                assertThat(list.size).isEqualTo(generateOfflineProperties().size + 1)
            }
        }
        enableConnections()
        delay(CONNECTION_DELAY)
    }

    @Test
    fun testUpdatePropertyOnline(): Unit = runBlocking { // Ok
        launch(Dispatchers.Main) {
            val propertyToUpdate = mPropertyUseCase.getPropertyWithIdFlow("1").first()

            propertyToUpdate.price = 10.0

            mPropertyUseCase.updateProperty(propertyToUpdate)

            val newProperty = mPropertyUseCase.getPropertyWithIdFlow("1").first()
            assertThat(newProperty.price).isEqualTo(10.0)
        }
    }

    @Test
    fun testUpdatePropertyOffline(): Unit = runBlocking { // Ok
        disableConnections()
        delay(CONNECTION_DELAY)
        launch(Dispatchers.Main) {
            val propertyToUpdate = mPropertyUseCase.getPropertyWithIdFlow("1").first()

            propertyToUpdate.surface = 60.5

            mPropertyUseCase.updateProperty(propertyToUpdate)

            val newProperty = mPropertyUseCase.getPropertyWithIdFlow("1").first()
            assertThat(newProperty.surface).isEqualTo(60.5)
        }
        enableConnections()
        delay(CONNECTION_DELAY)
    }

    @ExperimentalTime
    @Test
    fun testUploadPendingProperties(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyToAdd = Property(
                id = "201",
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
                mapPictureUrl = getStaticMapUrl(40.7589408497391, -73.97983110154246))

            mPropertyUseCase.stateFlow.test {
                assertThat(awaitItem()).isInstanceOf(State.Idle::class.java)

                mPropertyUseCase.fetchProperties()
                assertThat(awaitItem()).isInstanceOf(State.Download.Downloading::class.java)

                var result = awaitItem()
                assertThat(result).isInstanceOf(State.Download.DownloadSuccess::class.java)

                val initialListSize = (result as State.Download.DownloadSuccess).propertiesList.size

                disableConnections()
                delay(CONNECTION_DELAY)

                mPropertyUseCase.addProperty(propertyToAdd)
                assertThat(awaitItem()).isInstanceOf(State.OfflineSuccess::class.java)
                assertThat(awaitItem()).isInstanceOf(State.Upload.Error::class.java)

                mPropertyUseCase.fetchProperties()
                assertThat(awaitItem()).isInstanceOf(State.Download.Error::class.java)

                result = awaitItem()
                val offlineListSize = (result as State.OfflineSuccess).propertiesList.size

                assertThat(offlineListSize).isEqualTo(initialListSize+1)

                enableConnections()
                delay(CONNECTION_DELAY)

                mPropertyUseCase.uploadPendingProperties()
                assertThat(awaitItem()).isInstanceOf(State.Upload.Uploading::class.java)
                assertThat(awaitItem()).isInstanceOf(State.Upload.UploadSuccess.Empty::class.java)

                mPropertyUseCase.fetchProperties()

                result = awaitItem()
                assertThat(result).isInstanceOf(State.Download.DownloadSuccess::class.java)

                val onlineListSize = (result as State.Download.DownloadSuccess).propertiesList.size

                assertThat(onlineListSize).isEqualTo(initialListSize+1)
            }
        }
    }

    @ExperimentalTime
    @Test
    fun testResetState(): Unit = runBlocking {
        launch(Dispatchers.Main) {

            mPropertyUseCase.stateFlow.test {
                assertThat(awaitItem()).isInstanceOf(State.Idle::class.java)

                mPropertyUseCase.fetchProperties()
                assertThat(awaitItem()).isInstanceOf(State.Download.Downloading::class.java)
                assertThat(awaitItem()).isInstanceOf(State.Download.DownloadSuccess::class.java)

                mPropertyUseCase.resetState()
                assertThat(awaitItem()).isInstanceOf(State.Idle::class.java)
            }
        }
    }

    @ExperimentalTime
    @Test
    fun testGetPropertyWithFiltersAndRemoveFilters(): Unit = runBlocking {
        launch(Dispatchers.Main) {
            mPropertyUseCase.stateFlow.test {
                assertThat(awaitItem()).isInstanceOf(State.Idle::class.java)

                val propertyFilter = PropertyFilter(
                    minPrice = 100000,
                    maxPrice = 1000000)

                mPropertyUseCase.getPropertyWithFilters(propertyFilter)
                var result = awaitItem()
                assertThat(result).isInstanceOf(State.Filter.Result::class.java)

                val listSize = (result as State.Filter.Result).propertiesList.size
                assertThat(listSize).isEqualTo(5)

                mPropertyUseCase.removeFilters()
                assertThat(awaitItem()).isInstanceOf(State.Filter.Clear::class.java)
                result = awaitItem()
                assertThat(result).isInstanceOf(State.OfflineSuccess::class.java)
            }
        }
    }
}