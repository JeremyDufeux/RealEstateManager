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
import com.openclassrooms.realestatemanager.models.ui.PropertyUiDetailsView
import com.openclassrooms.realestatemanager.ui.mediaViewer.BUNDLE_KEY_MEDIA_LIST
import com.openclassrooms.realestatemanager.ui.mediaViewer.BUNDLE_KEY_SELECTED_MEDIA_INDEX
import com.openclassrooms.realestatemanager.ui.mediaViewer.MediaViewerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment(), DetailsMediaListAdapter.MediaListener {
    private val mViewModel: DetailsFragmentViewModel by viewModels()
    private lateinit var mBinding : FragmentDetailsBinding
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

        return mBinding.root
    }

    private fun configureRecyclerViews() {
        mBinding.fragmentDetailMediaRv.adapter = mMediaAdapter
        mBinding.fragmentDetailMediaRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    fun setPropertyId(propertyId: String) {
        mViewModel.setPropertyId(propertyId)
    }

    private val propertyObserver = Observer<PropertyUiDetailsView> { property ->

        mMediaAdapter.submitList(property.mediaList)

        mBinding.fragmentDetailPointOfInterestCg.removeAllViews()

        property.pointOfInterestList.map {
            val image = ResourcesCompat.getDrawable(requireContext().resources, it.icon, null)
            val chip = Chip(requireContext())
            chip.text = getString(it.description)
            chip.tag = it
            chip.chipIcon = image
            chip.setChipIconTintResource( R.color.colorAccent)
            chip.isClickable = false
            mBinding.fragmentDetailPointOfInterestCg.addView(chip)
        }

        Glide.with(this)
            .load(property.mapPictureUrl)
            .centerCrop()
            .timeout(2000)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mBinding.fragmentDetailMapIv)

        mBinding.apply {
            fragmentDetailTypeTv.text = property.type.description

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

            mBinding.fragmentDetailPointOfInterestCg.visibility = property.pointsOfInterestVisibility
            mBinding.fragmentDetailPointOfInterestIv.visibility = property.pointsOfInterestVisibility
            mBinding.fragmentDetailPointOfInterestTitleTv.visibility = property.pointsOfInterestVisibility

            fragmentDetailPostDateTv.text = property.postDate

            fragmentDetailSoldDateTv.text = property.soldDate
            fragmentDetailSoldDateTv.visibility = property.soldDateVisibility
            fragmentDetailSoldDateTitleTv.visibility = property.soldDateVisibility

            fragmentDetailAgentTv.text = property.agentName
            fragmentDetailAgentTv.visibility = property.agentVisibility
            fragmentDetailAgentIv.visibility = property.agentVisibility
            fragmentDetailAgentTitleTv.visibility = property.agentVisibility
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
        val arrayList = mViewModel.propertyLiveData.value?.mediaList?.toList() as ArrayList
        intent.putParcelableArrayListExtra(BUNDLE_KEY_MEDIA_LIST, arrayList )
        intent.putExtra(BUNDLE_KEY_SELECTED_MEDIA_INDEX, position)
        startActivity(intent)
    }
}