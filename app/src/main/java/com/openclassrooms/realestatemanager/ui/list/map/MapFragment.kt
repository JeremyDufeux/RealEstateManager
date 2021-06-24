package com.openclassrooms.realestatemanager.ui.list.map

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.ui.details.BUNDLE_KEY_PROPERTY_ID
import com.openclassrooms.realestatemanager.ui.details.DetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


private const val TAG = "MapFragment"
const val DEFAULT_ZOOM_VALUE = 15f

@AndroidEntryPoint
class MapFragment : Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener{

    private val mViewModel: MapFragmentViewModel by viewModels()
    private lateinit var mBinding : FragmentMapBinding
    private lateinit var mMap : GoogleMap
    lateinit var mLocation: Location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureLocationClient()
    }

    private fun configureLocationClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {

                locationResult ?: return
                for (location in locationResult.locations){
                    mLocation = location
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mBinding = FragmentMapBinding.inflate(layoutInflater)
        mBinding.mapViewFragmentLocationBtn.setOnClickListener { requestFocusToLocation() }

        configureMaps()

        return mBinding.root
    }

    private fun configureMaps(){
        val mapFragment : SupportMapFragment = childFragmentManager.findFragmentById(R.id.map_view_fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.setOnMarkerClickListener(this)
        val cameraUpdate : CameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(40.75607001232621, -73.98297695576221), 12F)
        mMap.moveCamera(cameraUpdate)
        startObserver()
        enableLocation()
    }

    private fun startObserver() {
        mViewModel.propertyListLiveData.observe(this, propertyListObserver)
    }

    private val propertyListObserver = Observer<List<Property>> {
        mMap.clear()

        for(property in it){
            if(property.latitude != 0.0 && property.longitude != 0.0) {
                val markerOptions = MarkerOptions()
                markerOptions.apply {
                    position(LatLng(property.latitude, property.longitude))
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
                }
                val marker = mMap.addMarker(markerOptions)
                marker?.tag = property.id
            }
        }
    }

    // ---------------
    // Map interactions
    // ---------------

    override fun onMarkerClick(marker : Marker): Boolean {
        marker.tag?.let {
            val intent = Intent(requireContext(), DetailsActivity::class.java)
            intent.putExtra(BUNDLE_KEY_PROPERTY_ID, it as String)
            startActivity(intent)
        }
        return true
    }

    private fun requestFocusToLocation() {
        if (hasLocationPermission()) {
            if(this::mLocation.isInitialized) {
                focusToLocation()
            }
            else{
                configureLocation()
            }
        } else {
            enableLocation()
        }
    }

    private fun focusToLocation() {
        val latLng = LatLng(mLocation.latitude, mLocation.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_VALUE)
        mMap.animateCamera(cameraUpdate)
    }

    // ---------------
    // Location permissions
    // ---------------

    private fun enableLocation() {
        when {
            hasLocationPermission() -> {
                configureLocation()
            }
            else -> {
                showLocationRequestDialog()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun configureLocation() { // Todo
        mMap.isMyLocationEnabled = true

        lifecycleScope.launch(Dispatchers.Main) {
            getLastLoc()
            focusToLocation()
        }

        val locationRequest = LocationRequest.create()
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastLoc() : Unit = suspendCoroutine { continuation ->
        mFusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                mLocation = location
           }
            continuation.resume(Unit)
        }
    }

    private fun showLocationRequestDialog() {
        val alertDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()
        alertDialog.setTitle(getString(R.string.location_request))
        alertDialog.setMessage(getString(R.string.location_request_dialog_message))
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
            openRequestPermissionLauncher()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun openRequestPermissionLauncher(){
        requestPermissionLauncher.launch(
            permission.ACCESS_COARSE_LOCATION)
    }

    private fun hasLocationPermission() : Boolean{
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {
            isGranted: Boolean ->
                if (isGranted) {
                    enableLocation()
                }
                else {
                    showLocationDenialDialog()
                }

    }

    private fun showLocationDenialDialog() {
        val alertDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()
        alertDialog.setTitle(getString(R.string.location_permission_denied))
        alertDialog.setMessage(getString(R.string.location_permission_denied_dialog_message))
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    // ---------------
    // On pause actions
    // ---------------

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }
}