package com.openclassrooms.realestatemanager.extensions

import com.openclassrooms.realestatemanager.BuildConfig

fun getGeoApifyUrl(latitude : Double, longitude : Double) : String {
    val baseUrl = "https://maps.geoapify.com/v1/staticmap?style=osm-carto&width=600&height=400&center=lonlat:%s,%s&zoom=17&marker=lonlat:%s,%s;color:%%23ff6f00;size:xx-large&apiKey=%s"
    return baseUrl.format(longitude, latitude, longitude, latitude, BuildConfig.GEOAPIFY_API_KEY)
}
