package com.openclassrooms.realestatemanager.database

import com.openclassrooms.realestatemanager.models.PropertyFilter
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import junit.framework.TestCase
import org.junit.Test

class SqlQueryBuilderTest: TestCase() {

    @Test
    fun testBuilderWithFilterMinPrice() {
        val propertyFilter = PropertyFilter(
            minPrice = 10000)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '10000' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterPriceAndSurface : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterMaxPrice() {
        val propertyFilter = PropertyFilter(
            maxPrice = 1000000)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price <= '1000000' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterPriceAndSurface : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterMinAndMaxPrice() {
        val propertyFilter = PropertyFilter(
            minPrice = 10000,
            maxPrice = 1000000)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '10000' " +
                "AND price <= '1000000' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterPriceAndSurface : $expectedQuery")

        assertEquals(expectedQuery, query)
    }
    
    @Test
    fun testBuilderWithFilterMinSurface() {
        val propertyFilter = PropertyFilter(
            minSurface = 10000)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE surface >= '10000' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterSurfaceAndSurface : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterMaxSurface() {
        val propertyFilter = PropertyFilter(
            maxSurface = 1000000)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE surface <= '1000000' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterSurfaceAndSurface : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterMinAndMaxSurface() {
        val propertyFilter = PropertyFilter(
            minSurface = 10000,
            maxSurface = 1000000)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE surface >= '10000' " +
                "AND surface <= '1000000' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterSurfaceAndSurface : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterPriceAndSurfaceWithBigNumbers() {
        val propertyFilter = PropertyFilter(
            minPrice = 12345678909,
            maxPrice = 1_234_567_890_123_456,
            minSurface = 12345678909,
            maxSurface = 1234567890123456)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '12345678909' AND price <= '1234567890123456' " +
                "AND surface >= '12345678909' AND surface <= '1234567890123456' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterPriceAndSurfaceWithBigNumbers : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterOneType() {
        val propertyTypeList = mutableListOf(PropertyType.HOUSE)
        val propertyFilter = PropertyFilter(
            propertyTypeList = propertyTypeList)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE (type = 'HOUSE') " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterOneType : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterMultipleType() {
        val propertyTypeList = mutableListOf(PropertyType.FLAT, PropertyType.LAND, PropertyType.TRIPLEX)
        val propertyFilter = PropertyFilter(propertyTypeList = propertyTypeList)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE (type = 'FLAT' OR type = 'LAND' OR type = 'TRIPLEX') " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterMultipleType : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterOnePoi() {
        val pointOfInterest = mutableListOf(PointOfInterest.PARKING)
        val propertyFilter = PropertyFilter(
            pointOfInterestList = pointOfInterest)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE (EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
                "WHERE properties.propertyId = poi.propertyId AND description = 'PARKING')) " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterOnePoi : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterMultiplePoi() {
        val pointOfInterest = mutableListOf(PointOfInterest.PARKING, PointOfInterest.SCHOOL, PointOfInterest.FITNESS_CLUB)
        val propertyFilter = PropertyFilter(
            pointOfInterestList = pointOfInterest)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE (EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
                "WHERE properties.propertyId = poi.propertyId AND description = 'PARKING')) " +
                "AND (EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
                "WHERE properties.propertyId = poi.propertyId AND description = 'SCHOOL')) " +
                "AND (EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
                "WHERE properties.propertyId = poi.propertyId AND description = 'FITNESS_CLUB')) " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterMultiplePoi : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterCity() {
        val propertyFilter = PropertyFilter(
            city = "Manhattan")

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE city LIKE '%Manhattan%' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterMultiplePoi : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterRoomAmount() {
        val propertyFilter = PropertyFilter(
            roomsAmount = 5)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE roomsAmount >= '5' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterRoomAmount : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterBathroomAmount() {
        val propertyFilter = PropertyFilter(
            bathroomsAmount = 5)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE bathroomsAmount >= '5' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterBathroomAmount : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterBedroomAmount() {
        val propertyFilter = PropertyFilter(
            bedroomsAmount = 2)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE bedroomsAmount >= '2' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterBedroomAmount : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterMediasAmount() {
        val propertyFilter = PropertyFilter(
            mediasAmount = 2)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT *" +
                ", COUNT(media_items.propertyId) as mediasAmount " +
                "FROM properties LEFT JOIN media_items ON (media_items.propertyId = properties.propertyId) " +
                "GROUP BY properties.propertyId " +
                "HAVING mediasAmount >= 2 " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterMediasAmount : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterAvailable() {
        val propertyFilter = PropertyFilter(
            available = true)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE soldDate IS NULL " +
                "AND postDate >= '0' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterAvailable : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterAvailableAndPostDate() {
        val propertyFilter = PropertyFilter(
            available = true,
            postDate = 1621500768764)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE soldDate IS NULL " +
                "AND postDate >= '1621500768764' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterAvailableAndPostDate : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterSold() {
        val propertyFilter = PropertyFilter(
            sold = true)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE soldDate IS NOT NULL " +
                "AND soldDate >= '0' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterSold : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterSoldAndSoldDate() {
        val propertyFilter = PropertyFilter(
            sold = true,
            soldDate = 1631263968760)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE soldDate IS NOT NULL " +
                "AND soldDate >= '1631263968760' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterSold : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithAllFilterButNotSold() {
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
            postDate = 1615024364761)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT *, COUNT(media_items.propertyId) as mediasAmount " +
                "FROM properties " +
                "LEFT JOIN media_items " +
                "ON (media_items.propertyId = properties.propertyId) " +
                "GROUP BY properties.propertyId " +
                "HAVING price >= '100' " +
                "AND price <= '100000000' " +
                "AND surface >= '200' " +
                "AND surface <= '8000' " +
                "AND (type = 'FLAT' OR type = 'LAND' OR type = 'TRIPLEX') " +

                "AND (EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
                "WHERE properties.propertyId = poi.propertyId AND description = 'PARK')) " +
                "AND (EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
                "WHERE properties.propertyId = poi.propertyId AND description = 'SCHOOL')) " +
                "AND (EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
                "WHERE properties.propertyId = poi.propertyId AND description = 'GROCERY')) " +

                "AND city LIKE '%Manhattan%' " +
                "AND roomsAmount >= '5' " +
                "AND bedroomsAmount >= '2' " +
                "AND bathroomsAmount >= '2' " +
                "AND mediasAmount >= 2 " +
                "AND soldDate IS NULL " +
                "AND postDate >= '1615024364761' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterSold : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithAllFilterButSold() {
        val propertyTypeList = mutableListOf(PropertyType.FLAT, PropertyType.LAND, PropertyType.TRIPLEX)
        val pointOfInterest = mutableListOf(PointOfInterest.PARK, PointOfInterest.SCHOOL, PointOfInterest.GROCERY)
        val propertyFilter = PropertyFilter(
            minPrice = 100,
            maxPrice = 400000,
            minSurface = 200,
            maxSurface = 2000,
            propertyTypeList = propertyTypeList,
            pointOfInterestList = pointOfInterest,
            city = "Manhattan",
            roomsAmount = 5,
            bathroomsAmount = 2,
            bedroomsAmount = 2,
            mediasAmount = 2,
            sold = true,
            soldDate = 1631436758798)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT *, COUNT(media_items.propertyId) as mediasAmount " +
                "FROM properties " +
                "LEFT JOIN media_items " +
                "ON (media_items.propertyId = properties.propertyId) " +
                "GROUP BY properties.propertyId " +
                "HAVING price >= '100' " +
                "AND price <= '400000' " +
                "AND surface >= '200' " +
                "AND surface <= '2000' " +
                "AND (type = 'FLAT' OR type = 'LAND' OR type = 'TRIPLEX') " +

                "AND (EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
                "WHERE properties.propertyId = poi.propertyId AND description = 'PARK')) " +
                "AND (EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
                "WHERE properties.propertyId = poi.propertyId AND description = 'SCHOOL')) " +
                "AND (EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
                "WHERE properties.propertyId = poi.propertyId AND description = 'GROCERY')) " +

                "AND city LIKE '%Manhattan%' " +
                "AND roomsAmount >= '5' " +
                "AND bedroomsAmount >= '2' " +
                "AND bathroomsAmount >= '2' " +
                "AND mediasAmount >= 2 " +
                "AND soldDate IS NOT NULL " +
                "AND soldDate >= '1631436758798' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterSold : $expectedQuery")

        assertEquals(expectedQuery, query)
    }
}