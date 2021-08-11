package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.utils.Utils.*
import junit.framework.TestCase
import org.junit.Test

class ConversionsTest: TestCase() {

    @Test
    fun testConvertSquareFootToSquareMeters() {
        val squareFoot = 50.0
        val squareMeter = convertSquareFootToSquareMeters(squareFoot)
        assertEquals(4.645152000, squareMeter)
        assertEquals(4.65, round(squareMeter, 2))
    }

    @Test
    fun testConvertSquareMetersToSquareFoot() {
        val squareMeter = 50.0
        val squareFoot = convertSquareMetersToSquareFoot(squareMeter)
        assertEquals(538.195520835500, squareFoot)
        assertEquals(538.2, round(squareFoot, 2))
    }

    @Test
    fun testConvertSquareFootToSquareMetersToSquareFoot() {
        val squareFoot = 50.0
        val squareMeter = convertSquareFootToSquareMeters(squareFoot)
        assertEquals(4.64515200, squareMeter)

        val convertedSquareFoot =  convertSquareMetersToSquareFoot(squareMeter)
        assertEquals(squareFoot, round(convertedSquareFoot, 2))
    }

    @Test
    fun testConvertSquareMetersToSquareFootToSquareMeters() {
        val squareMeter = 50.0
        val squareFoot = convertSquareMetersToSquareFoot(squareMeter)
        assertEquals(538.195520835500, squareFoot)

        val convertedSquareMeters = convertSquareFootToSquareMeters(squareFoot)
        assertEquals(squareMeter, round(convertedSquareMeters, 2))
    }

}