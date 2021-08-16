package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.BuildConfig

fun getStaticMapUrl(latitude : Double?, longitude : Double?) : String? {
    val baseUrl = "https://maps.googleapis.com/maps/api/staticmap" +
            "?center=%s,%s" +
            "&zoom=15&" +
            "size=600x400" +
            "&maptype=roadmap" +
            "&markers=color:0xff4081|%s,%s" +
            "&key=%s"

    return if(latitude != null && longitude != null){
        baseUrl.format(latitude, longitude, latitude, longitude, BuildConfig.GOOGLE_MAPS_STATIC_API_KEY)
    } else {
        null
    }
}