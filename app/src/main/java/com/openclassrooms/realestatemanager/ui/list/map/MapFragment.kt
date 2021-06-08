package com.openclassrooms.realestatemanager.ui.list.map

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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

private const val TAG = "MapFragment"

@AndroidEntryPoint
class MapFragment : Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private val mViewModel: MapFragmentViewModel by viewModels()
    lateinit var mBinding : FragmentMapBinding
    lateinit var mMap : GoogleMap

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        mBinding = FragmentMapBinding.inflate(layoutInflater)

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
    }

    private fun startObserver() {
        mViewModel.propertyListLiveData.observe(this, propertyListObserver)
    }

    private val propertyListObserver = Observer<List<Property>> {
        mMap.clear()

        for(property in it){
            val markerOptions = MarkerOptions()
            markerOptions.apply {
                position(LatLng(property.latitude, property.longitude))
                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
            }
            val marker = mMap.addMarker(markerOptions)
            marker?.tag = property.id

        }
    }

    override fun onMarkerClick(marker : Marker): Boolean {
        Log.d(TAG, "onMarkerClick: ${marker.tag}")
        marker.tag?.let {
            val intent = Intent(requireContext(), DetailsActivity::class.java)
            intent.putExtra(BUNDLE_KEY_PROPERTY_ID, it as String)
            startActivity(intent)
        }
        return true
    }
}