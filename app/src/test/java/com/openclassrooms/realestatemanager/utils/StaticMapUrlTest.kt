package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.BuildConfig
import junit.framework.TestCase
import org.junit.Test

class StaticMapUrlTest: TestCase() {

    @Test
    fun testGetStaticMapUrlAndAssertData(){
        val url = getStaticMapUrl(50.0, 60.0)

        assertEquals("https://maps.googleapis.com/maps/api/staticmap?center=50.0,60.0&zoom=15&size=600x400&maptype=roadmap&markers=color:0xff4081|50.0,60.0&key=${BuildConfig.GOOGLE_MAPS_STATIC_API_KEY}", url)
    }

    @Test
    fun testGetStaticMapUrlWithNullValuesAndAssertData(){
        val url = getStaticMapUrl(null, null)

        assertEquals(null, url)
    }
}