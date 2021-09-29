package com.openclassrooms.realestatemanager.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentDetailsBinding
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.ui.PropertyUiDetailsView
import com.openclassrooms.realestatemanager.ui.mediaViewer.BUNDLE_KEY_MEDIA_LIST
import com.openclassrooms.realestatemanager.ui.mediaViewer.BUNDLE_KEY_SELECTED_MEDIA_INDEX
import com.openclassrooms.realestatemanager.ui.mediaViewer.MediaViewerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment(), DetailsMediaListAdapter.MediaListener {
    private val mViewModel: DetailsFragmentViewModel by viewModels()
    private var mBinding : FragmentDetailsBinding? = null
    private val mMediaAdapter = DetailsMediaListAdapter(this)

    companion object {
        fun newInstance() = DetailsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureViewModel()
    }

    private fun configureViewModel() {
        mViewModel.propertyLiveData.observe(this, propertyObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mBinding = FragmentDetailsBinding.inflate(layoutInflater)

        configureRecyclerViews()

        return mBinding!!.root
    }

    private fun configureRecyclerViews() {
        mBinding?.apply {
            fragmentDetailMediaRv.adapter = mMediaAdapter
            fragmentDetailMediaRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    fun setPropertyId(propertyId: String) {
        mViewModel.setPropertyId(propertyId)
    }

    private val propertyObserver = Observer<PropertyUiDetailsView> { property ->

        mMediaAdapter.submitList(property.mediaList)

        mBinding?.apply {
            fragmentDetailPointOfInterestCg.removeAllViews()

            property.pointOfInterestList.map {
                val image = ResourcesCompat.getDrawable(requireContext().resources, it.icon, null)
                val chip = Chip(requireContext())
                chip.text = getString(it.description)
                chip.tag = it
                chip.chipIcon = image
                chip.setChipIconTintResource( R.color.colorAccent)
                chip.isClickable = false
                fragmentDetailPointOfInterestCg.addView(chip)
            }

            Glide.with(this@DetailsFragment)
                .load(property.mapPictureUrl)
                .centerCrop()
                .timeout(2000)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(fragmentDetailMapIv)

            fragmentDetailTypeTv.text = getString(property.type.description)

            fragmentDetailPriceTv.text = property.priceString
            fragmentDetailPriceTv.visibility = property.priceVisibility

            fragmentDetailDescriptionTv.text = property.description
            fragmentDetailDescriptionTv.visibility = property.descriptionVisibility
            fragmentDetailDescriptionTitleTv.visibility = property.descriptionVisibility

            fragmentDetailSurfaceTv.text = property.surfaceString
            fragmentDetailSurfaceTv.visibility = property.surfaceVisibility
            fragmentDetailSurfaceTitleTv.visibility = property.surfaceVisibility
            fragmentDetailSurfaceIv.visibility = property.surfaceVisibility

            fragmentDetailRoomsTv.text = property.roomsAmount
            fragmentDetailRoomsTv.visibility = property.roomsVisibility
            fragmentDetailRoomsIv.visibility = property.roomsVisibility
            fragmentDetailRoomsTitleTv.visibility = property.roomsVisibility

            fragmentDetailBathroomsTv.text = property.bathroomsAmount
            fragmentDetailBathroomsTv.visibility = property.bathroomsVisibility
            fragmentDetailBathroomsIv.visibility = property.bathroomsVisibility
            fragmentDetailBathroomsTitleTv.visibility = property.bathroomsVisibility

            fragmentDetailBedroomsTv.text = property.bedroomsAmount
            fragmentDetailBedroomsTv.visibility = property.bedroomsVisibility
            fragmentDetailBedroomsIv.visibility = property.bedroomsVisibility
            fragmentDetailBedroomsTitleTv.visibility = property.bedroomsVisibility

            fragmentDetailLocationTv.text = property.address
            fragmentDetailLocationTv.visibility = property.addressVisibility
            fragmentDetailLocationIv.visibility = property.addressVisibility
            fragmentDetailLocationTitleTv.visibility = property.addressVisibility

            fragmentDetailMapIv.visibility = property.mapVisibility
            fragmentDetailMapCv.visibility = property.mapVisibility
            fragmentDetailMapCv.setOnClickListener { openGoogleMaps(property.latitude, property.longitude) }

            fragmentDetailPointOfInterestCg.visibility = property.pointsOfInterestVisibility
            fragmentDetailPointOfInterestIv.visibility = property.pointsOfInterestVisibility
            fragmentDetailPointOfInterestTitleTv.visibility = property.pointsOfInterestVisibility

            fragmentDetailPostDateTv.text = property.postDate

            fragmentDetailSoldDateTv.text = property.soldDate
            fragmentDetailSoldDateTv.visibility = property.soldDateVisibility
            fragmentDetailSoldDateTitleTv.visibility = property.soldDateVisibility

            fragmentDetailAgentTv.text = property.agentName
            fragmentDetailAgentTv.visibility = property.agentVisibility
            fragmentDetailAgentIv.visibility = property.agentVisibility
            fragmentDetailAgentTitleTv.visibility = property.agentVisibility

            fragmentDetailSoldTv.visibility = property.soldDateVisibility
        }
    }

    private fun openGoogleMaps(latitude: Double?, longitude: Double?){
        val gmmIntentUri: Uri = Uri.parse("geo:0,0?q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(mapIntent)
        }
    }

    override fun onMediaClick(position: Int) {
        val intent = Intent(requireContext(), MediaViewerActivity::class.java)

        val mediaList = mViewModel.propertyLiveData.value?.mediaList?.toList()

        val arrayList: java.util.ArrayList<MediaItem> = if(mediaList?.size == 1) {
            arrayListOf(mediaList[0])
        } else {
            mediaList as ArrayList<MediaItem>
        }

        intent.putParcelableArrayListExtra(BUNDLE_KEY_MEDIA_LIST, arrayList)
        intent.putExtra(BUNDLE_KEY_SELECTED_MEDIA_INDEX, position)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        mBinding = null
    }
}