package com.openclassrooms.realestatemanager.ui.list

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.models.ui.PropertyUiMapView
import dagger.hilt.android.AndroidEntryPoint

const val DEFAULT_ZOOM_VALUE = 15f
const val LOCATION_PERMISSION = permission.ACCESS_FINE_LOCATION

@AndroidEntryPoint
class MapFragment : Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener{

    private val mViewModel: ListViewModel by activityViewModels()
    private var mBinding : FragmentMapBinding? = null
    private lateinit var mMap : GoogleMap
    private lateinit var mLocation: Location

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mBinding = FragmentMapBinding.inflate(layoutInflater)
        mBinding?.mapViewFragmentLocationBtn?.setOnClickListener { requestFocusToLocation() }

        configureMaps()

        return mBinding!!.root
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
        mViewModel.stateLiveData.observe(this, stateObserver)
        mViewModel.propertiesUiMapViewLiveData.observe(this, propertiesObserver)
    }

    private val stateObserver = Observer<State> { state ->
        when(state){
            is State.Download.Downloading -> {
                mBinding?.mapViewFragmentPb?.visibility = View.VISIBLE
            }
            is State.Download.DownloadSuccess -> {
                mBinding?.mapViewFragmentPb?.visibility = View.GONE
            }
            is State.Download.Error -> {
                mBinding?.mapViewFragmentPb?.visibility = View.GONE
            }
            else -> {}
        }
    }

    private val propertiesObserver = Observer<List<PropertyUiMapView>> { properties ->
        updateMap(properties)
    }

    private fun updateMap(properties: List<PropertyUiMapView>){
        mMap.clear()

        for(property in properties){
            if(property.latitude != null && property.longitude != null) {
                val markerOptions = MarkerOptions()
                markerOptions.apply {
                    position(LatLng(property.latitude!!, property.longitude!!))
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
            mViewModel.setSelectedPropertyId(it as String)
        }
        return true
    }

    private fun requestFocusToLocation() {
        if (hasLocationPermission()) {
            if(!mViewModel.locationStarted) {
                focusToLocation()
            }
        } else {
            enableLocation()
        }
    }

    private fun focusToLocation() {
        if(this::mLocation.isInitialized) {
            val latLng = LatLng(mLocation.latitude, mLocation.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_VALUE)
            mMap.animateCamera(cameraUpdate)
        }
    }

    // ---------------
    // Location permissions
    // ---------------

    @SuppressLint("MissingPermission")
    private fun enableLocation() {
        when {
            hasLocationPermission() -> {
                if(!mViewModel.locationStarted) {
                    mViewModel.startLocationUpdates()
                    mViewModel.location.observe(viewLifecycleOwner, { mLocation = it })
                }
                mMap.isMyLocationEnabled = true
            }
            else -> {
                showLocationRequestDialog()
            }
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
        requestPermissionLauncher.launch(LOCATION_PERMISSION)
    }

    private fun hasLocationPermission() : Boolean{
        return ContextCompat.checkSelfPermission(
            requireContext(),
            LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
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

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()

        mBinding = null
    }

    private fun stopLocationUpdates() {
        mViewModel.stopLocationUpdates()
    }
}