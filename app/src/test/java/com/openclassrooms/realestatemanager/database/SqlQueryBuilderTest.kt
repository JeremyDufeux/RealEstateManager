package com.openclassrooms.realestatemanager.database

import com.openclassrooms.realestatemanager.models.PropertyFilter
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import junit.framework.TestCase
import org.junit.Test

class SqlQueryBuilderTest: TestCase() {

    @Test
    fun testBuilderWithFilterPriceAndSurface() {
        val propertyFilter = PropertyFilter(
            minPrice = 0.5,
            maxPrice = 1000000.0,
            minSurface = 0.5,
            maxSurface = 1000.0)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '0.5' AND price <= '1000000.0' " +
                "AND surface >= '0.5' AND surface <= '1000.0' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterPriceAndSurface : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterPriceAndSurfaceWithBigNumbers() {
        val propertyFilter = PropertyFilter(
            minPrice = 12345678909.5,
            maxPrice = 1_234_567_890_123_456.5,
            minSurface = 12345678909.5,
            maxSurface = 1234567890123456.5)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '12345678909.5' AND price <= '1234567890123456.5' " +
                "AND surface >= '12345678909.5' AND surface <= '1234567890123456.5' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterPriceAndSurfaceWithBigNumbers : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterOneType() {
        val propertyTypeList = mutableListOf(PropertyType.HOUSE)
        val propertyFilter = PropertyFilter(
            minPrice = 0.0,
            maxPrice = 1000000.0,
            minSurface = 0.0,
            maxSurface = 1000.0,
            propertyTypeList = propertyTypeList)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '0.0' AND price <= '1000000.0' " +
                "AND surface >= '0.0' AND surface <= '1000.0' " +
                "AND (type = 'HOUSE') " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterOneType : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterMultipleType() {
        val propertyTypeList = mutableListOf(PropertyType.FLAT, PropertyType.LAND, PropertyType.TRIPLEX)
        val propertyFilter = PropertyFilter(
            minPrice = 0.0,
            maxPrice = 1000000.0,
            minSurface = 0.0,
            maxSurface = 1000.0,
            propertyTypeList = propertyTypeList)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '0.0' AND price <= '1000000.0' " +
                "AND surface >= '0.0' AND surface <= '1000.0' " +
                "AND (type = 'FLAT' OR type = 'LAND' OR type = 'TRIPLEX') " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterMultipleType : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterOnePoi() {
        val pointOfInterest = mutableListOf(PointOfInterest.PARKING)
        val propertyFilter = PropertyFilter(
            minPrice = 0.0,
            maxPrice = 1000000.0,
            minSurface = 0.0,
            maxSurface = 1000.0,
            pointOfInterestList = pointOfInterest)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '0.0' AND price <= '1000000.0' " +
                "AND surface >= '0.0' AND surface <= '1000.0' " +
                "AND (EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
                "WHERE properties.propertyId = poi.propertyId AND description = 'PARKING')) " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterOnePoi : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterMultiplePoi() {
        val pointOfInterest = mutableListOf(PointOfInterest.PARKING, PointOfInterest.SCHOOL, PointOfInterest.FITNESS_CLUB)
        val propertyFilter = PropertyFilter(
            minPrice = 0.0,
            maxPrice = 1000000.0,
            minSurface = 0.0,
            maxSurface = 1000.0,
            pointOfInterestList = pointOfInterest)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '0.0' AND price <= '1000000.0' " +
                "AND surface >= '0.0' AND surface <= '1000.0' " +
                "AND (EXISTS (SELECT * FROM properties_point_of_interest_cross_ref as poi " +
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
            minPrice = 0.0,
            maxPrice = 1000000.0,
            minSurface = 0.0,
            maxSurface = 10000.0,
            city = "Manhattan")

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '0.0' AND price <= '1000000.0' " +
                "AND surface >= '0.0' AND surface <= '10000.0' " +
                "AND city LIKE '%Manhattan%' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterMultiplePoi : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterRoomAmount() {
        val propertyFilter = PropertyFilter(
            minPrice = 0.0,
            maxPrice = 1000000.0,
            minSurface = 0.0,
            maxSurface = 1000.0,
            roomsAmount = 5)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '0.0' AND price <= '1000000.0' " +
                "AND surface >= '0.0' AND surface <= '1000.0' " +
                "AND roomsAmount >= '5' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterRoomAmount : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterBathroomAmount() {
        val propertyFilter = PropertyFilter(
            minPrice = 0.0,
            maxPrice = 1000000.0,
            minSurface = 0.0,
            maxSurface = 1000.0,
            bathroomsAmount = 5)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '0.0' AND price <= '1000000.0' " +
                "AND surface >= '0.0' AND surface <= '1000.0' " +
                "AND bathroomsAmount >= '5' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterBathroomAmount : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterBedroomAmount() {
        val propertyFilter = PropertyFilter(
            minPrice = 0.0,
            maxPrice = 1000000.0,
            minSurface = 0.0,
            maxSurface = 100000.0,
            bedroomsAmount = 2)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '0.0' AND price <= '1000000.0' " +
                "AND surface >= '0.0' AND surface <= '100000.0' " +
                "AND bedroomsAmount >= '2' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterBedroomAmount : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterMediasAmount() {
        val propertyFilter = PropertyFilter(
            minPrice = 0.0,
            maxPrice = 1000000.0,
            minSurface = 0.0,
            maxSurface = 100000.0,
            mediasAmount = 2)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT *" +
                ", COUNT(media_items.propertyId) as mediasAmount " +
                "FROM properties LEFT JOIN media_items ON (media_items.propertyId = properties.propertyId) " +
                "GROUP BY properties.propertyId " +
                "HAVING price >= '0.0' AND price <= '1000000.0' " +
                "AND surface >= '0.0' AND surface <= '100000.0' " +
                "AND mediasAmount >= 2 " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterMediasAmount : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterAvailable() {
        val propertyFilter = PropertyFilter(
            minPrice = 0.0,
            maxPrice = 1000000.0,
            minSurface = 0.0,
            maxSurface = 100000.0,
            available = true)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '0.0' AND price <= '1000000.0' " +
                "AND surface >= '0.0' AND surface <= '100000.0' " +
                "AND soldDate IS NULL " +
                "AND postDate >= '0' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterAvailable : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterAvailableAndPostDate() {
        val propertyFilter = PropertyFilter(
            minPrice = 0.0,
            maxPrice = 1000000.0,
            minSurface = 0.0,
            maxSurface = 100000.0,
            available = true,
            postDate = 1621500768764)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '0.0' AND price <= '1000000.0' " +
                "AND surface >= '0.0' AND surface <= '100000.0' " +
                "AND soldDate IS NULL " +
                "AND postDate >= '1621500768764' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterAvailableAndPostDate : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterSold() {
        val propertyFilter = PropertyFilter(
            minPrice = 0.0,
            maxPrice = 1000000.0,
            minSurface = 0.0,
            maxSurface = 100000.0,
            sold = true)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '0.0' AND price <= '1000000.0' " +
                "AND surface >= '0.0' AND surface <= '100000.0' " +
                "AND soldDate IS NOT NULL " +
                "AND soldDate >= '0' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterSold : $expectedQuery")

        assertEquals(expectedQuery, query)
    }

    @Test
    fun testBuilderWithFilterSoldAndSoldDate() {
        val propertyFilter = PropertyFilter(
            minPrice = 0.0,
            maxPrice = 1000000.0,
            minSurface = 0.0,
            maxSurface = 100000.0,
            sold = true,
            soldDate = 1631263968760)

        val query = constructSqlQuery(propertyFilter)
        val expectedQuery = "SELECT * " +
                "FROM properties " +
                "WHERE price >= '0.0' AND price <= '1000000.0' " +
                "AND surface >= '0.0' AND surface <= '100000.0' " +
                "AND soldDate IS NOT NULL " +
                "AND soldDate >= '1631263968760' " +
                "ORDER BY properties.postDate"

        println("Debug testBuilderWithFilterSold : $expectedQuery")

        assertEquals(expectedQuery, query)
    }


}