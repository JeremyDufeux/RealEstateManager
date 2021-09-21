package com.openclassrooms.realestatemanager.utils

import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.utils.Utils.*
import junit.framework.TestCase
import org.junit.Test

class ConversionsTest: TestCase() {

    @Test
    fun testConvertSquareFootToSquareMeters() {
        val squareFoot = 50.0
        val squareMeter = convertSquareFootToSquareMeters(squareFoot)
        assertThat(squareMeter).isEqualTo(4.645152000)
        assertThat(round(squareMeter, 2)).isEqualTo(4.65)
    }

    @Test
    fun testConvertSquareMetersToSquareFoot() {
        val squareMeter = 50.0
        val squareFoot = convertSquareMetersToSquareFoot(squareMeter)
        assertThat(squareFoot).isEqualTo(538.195520835500)
        assertThat(round(squareFoot, 2)).isEqualTo(538.2)
    }

    @Test
    fun testConvertSquareFootToSquareMetersToSquareFoot() {
        val squareFoot = 50.0
        val squareMeter = convertSquareFootToSquareMeters(squareFoot)
        assertThat(squareMeter).isEqualTo(4.64515200)

        val convertedSquareFoot =  convertSquareMetersToSquareFoot(squareMeter)
        assertThat(round(convertedSquareFoot, 2)).isEqualTo(squareFoot)
    }

    @Test
    fun testConvertSquareMetersToSquareFootToSquareMeters() {
        val squareMeter = 50.0
        val squareFoot = convertSquareMetersToSquareFoot(squareMeter)
        assertThat(squareFoot).isEqualTo(538.195520835500)

        val convertedSquareMeters = convertSquareFootToSquareMeters(squareFoot)
        assertThat(round(convertedSquareMeters, 2)).isEqualTo(squareMeter)
    }

}