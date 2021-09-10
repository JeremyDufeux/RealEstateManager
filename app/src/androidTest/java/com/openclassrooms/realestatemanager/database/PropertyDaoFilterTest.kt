package com.openclassrooms.realestatemanager.database

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.models.PropertyFilter
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
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
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PropertyDaoFilterTest{

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
    fun testFilterMinPriceAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                minPrice = 100000)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(7, propertyList.size)
        }
    }

    @Test
    fun testFilterMaxPriceAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                maxPrice = 1000000)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(5, propertyList.size)
        }
    }

    @Test
    fun testFilterMinAndMaxPriceAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                minPrice = 100000,
                maxPrice = 1000000)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(5, propertyList.size)
        }
    }

    @Test
    fun testFilterMinSurfaceAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                minSurface = 1000)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(5, propertyList.size)
        }
    }

    @Test
    fun testFilterMaxSurfaceAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                maxSurface = 3000)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(5, propertyList.size)
        }
    }

    @Test
    fun testFilterMinAndMaxSurfaceAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                minSurface = 1000,
                maxSurface = 3000)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(3, propertyList.size)
        }
    }

    @Test
    fun testFilterOneTypeAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyTypeList = mutableListOf(PropertyType.HOUSE)
            val propertyFilter = PropertyFilter(
                propertyTypeList = propertyTypeList)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(2, propertyList.size)
        }
    }

    @Test
    fun testFilterMultipleTypesAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyTypeList = mutableListOf(PropertyType.FLAT, PropertyType.LAND, PropertyType.TRIPLEX)
            val propertyFilter = PropertyFilter(
                propertyTypeList = propertyTypeList)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(4, propertyList.size)
        }
    }

    @Test
    fun testFilterOnePoiAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val pointOfInterest = mutableListOf(PointOfInterest.FITNESS_CLUB)
            val propertyFilter = PropertyFilter(
                pointOfInterestList = pointOfInterest)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(4, propertyList.size)
        }
    }

    @Test
    fun testFilterMultiplePoiAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val pointOfInterest = mutableListOf(PointOfInterest.FITNESS_CLUB, PointOfInterest.SCHOOL, PointOfInterest.PUBLIC_TRANSPORT)
            val propertyFilter = PropertyFilter(
                pointOfInterestList = pointOfInterest)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(3, propertyList.size)
        }
    }

    @Test
    fun testFilterCityAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                city = "Manhattan")

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(3, propertyList.size)
        }
    }

    @Test
    fun testFilterRoomsAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                roomsAmount = 8)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(4, propertyList.size)
        }
    }

    @Test
    fun testFilterBathroomsAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                bathroomsAmount = 2)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(4, propertyList.size)
        }
    }

    @Test
    fun testFilterBedroomsAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                bedroomsAmount = 3)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(3, propertyList.size)
        }
    }

    @Test
    fun testFilterMediasAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                mediasAmount = 5)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(3, propertyList.size)
        }
    }

    @Test
    fun testFilterIsAvailableAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                available = true)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(4, propertyList.size)
        }
    }

    @Test
    fun testFilterIsAvailableSinceAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                available = true,
                postDate = 1621500768764) // 20 may 2021

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(1, propertyList.size)
        }
    }

    @Test
    fun testFilterIsSoldAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                sold = true)

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(3, propertyList.size)
        }
    }

    @Test
    fun testFilterIsSoldSinceAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyFilter = PropertyFilter(
                sold = true,
                soldDate = 1631184830000)  // 9 september 2021

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(2, propertyList.size)
        }
    }

    @Test
    fun testFilterAllFilterButNotSoldAndAssertCount() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            val propertyTypeList = mutableListOf(PropertyType.FLAT, PropertyType.LAND, PropertyType.TRIPLEX)
            val pointOfInterest = mutableListOf(PointOfInterest.PARK, PointOfInterest.SCHOOL, PointOfInterest.GROCERY)
            val propertyFilter = PropertyFilter(
                minPrice = 100,
                maxPrice = 100000000,
                minSurface = 200,
                maxSurface = 8000,
                propertyTypeList = propertyTypeList,
                pointOfInterestList = pointOfInterest,
                city = "Manhattan",
                roomsAmount = 5,
                bathroomsAmount = 2,
                bedroomsAmount = 2,
                mediasAmount = 2,
                available = true,
                postDate = 1612483200000) // 5 February 2021

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(1, propertyList.size)
        }
    }

    @Test
    fun testFilterAllFilterButSoldAndAssertCount() : Unit = runBlocking {
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

            val queryString = constructSqlQuery(propertyFilter)
            val query = SimpleSQLiteQuery(queryString)

            val propertyList = propertyDao.getPropertyWithFilters(query)

            assertEquals(1, propertyList.size)
        }
    }
}