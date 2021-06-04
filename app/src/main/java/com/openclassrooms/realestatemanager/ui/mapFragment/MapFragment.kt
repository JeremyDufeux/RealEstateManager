package com.openclassrooms.realestatemanager.ui.mapFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.models.Property
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private val mViewModel: MapFragmentViewModel by viewModels()
    lateinit var mBinding : FragmentMapBinding
    lateinit var mMap : GoogleMap

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureViewModel()
    }

    private fun configureViewModel() {
        mViewModel.propertyListLiveData.observe(this, propertyListObserver)
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false
        val cameraUpdate : CameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(40.75607001232621, -73.98297695576221), 12F)
        mMap.moveCamera(cameraUpdate)
    }

    private val propertyListObserver = Observer<List<Property>> {
        mMap.clear()

        for(property in it){
            val markerOptions = MarkerOptions()
            markerOptions.apply {
                position(LatLng(property.latitude, property.longitude))
                icon(BitmapDescriptorFactory.fromResource(R.drawable.house))
            }
            val marker = mMap.addMarker(markerOptions)
            marker?.tag = property.id
        }
    }
}