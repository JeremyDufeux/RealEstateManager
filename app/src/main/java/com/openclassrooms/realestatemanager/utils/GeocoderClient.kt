package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class GeocoderClient @Inject constructor(@ApplicationContext private val mContext : Context) {

    val coder = Geocoder(mContext)

    fun getPropertyLocation(address1 : String, address2: String, city : String, postalCode : String, country : String) : LatLng?{
        val addressResult: List<Address?>

        return try {
            val address = "$address1 " +
                    "$address2 " +
                    "$city " +
                    "$postalCode " +
                    country
            addressResult = coder.getFromLocationName(address, 1)
            val latitude = addressResult[0]?.latitude!!
            val longitude = addressResult[0]?.longitude!!
            LatLng(latitude, longitude)
        } catch (e: Exception) {
            Timber.d("getPropertyLocation: address not found")
            null
        }
    }
}