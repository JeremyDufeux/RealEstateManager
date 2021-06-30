package com.openclassrooms.realestatemanager.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.HandlerThread
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "LocationRepository"

@Singleton
class LocationService @Inject constructor(@ApplicationContext private val mContext: Context) : LocationCallback(){

    private val _locationFlow = MutableStateFlow(Location(""))
    val locationFlow = _locationFlow.asStateFlow()

    private var _locationStarted = false
    val locationStarted: Boolean get() = _locationStarted
    
    private val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val mHandlerThread = HandlerThread("LocationUpdates")
        mHandlerThread.start()

        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        mFusedLocationClient.apply {
            lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    _locationFlow.value = location
                }
            }
            requestLocationUpdates(
                locationRequest,
                this@LocationService,
                mHandlerThread.looper
            )
        }

        _locationStarted = true
    }

    fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(this)

        _locationStarted = true
    }

    override fun onLocationResult(locationResult: LocationResult?) {
        locationResult ?: return
        for (location in locationResult.locations){
            _locationFlow.value = location
        }
    }
}