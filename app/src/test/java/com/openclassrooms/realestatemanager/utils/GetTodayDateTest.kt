package com.openclassrooms.realestatemanager.utils

import junit.framework.TestCase
import org.junit.Test

class GetTodayDateTest : TestCase(){

    @Test
    fun testFormatCalendarToString(){
        val formattedDate = formatCalendarToString(1631429951000)

        assertEquals("September 12, 2021", formattedDate)
    }
}