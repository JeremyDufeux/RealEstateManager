package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.utils.Utils.getTodayDate
import junit.framework.TestCase
import org.junit.Test
import java.util.*

class DateFormatTest : TestCase(){

    @Test
    fun testGetTodayDate(){
        val date = Calendar.getInstance().also {
            it.timeInMillis = 0
            it.set(2021, 8, 12) }.timeInMillis

        val formattedDate = getTodayDate(Date(date))

        assertEquals("12/09/2021", formattedDate)
    }

    @Test
    fun testFormatTimestampToString(){
        val formattedDate = formatTimestampToString(1631429951000)

        assertEquals("September 12, 2021", formattedDate)
    }
}