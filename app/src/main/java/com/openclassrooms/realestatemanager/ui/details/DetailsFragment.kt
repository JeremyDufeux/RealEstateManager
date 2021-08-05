package com.openclassrooms.realestatemanager.ui.details

import android.content.Intent
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
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.ui.mediaViewer.BUNDLE_KEY_MEDIA_LIST
import com.openclassrooms.realestatemanager.ui.mediaViewer.BUNDLE_KEY_SELECTED_MEDIA_INDEX
import com.openclassrooms.realestatemanager.ui.mediaViewer.MediaViewerActivity
import com.openclassrooms.realestatemanager.utils.formatCalendarToString
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

    private val propertyObserver = Observer<Property> { property ->

        mMediaAdapter.submitList(property.mediaList)

        property.pointOfInterestList.map {
            val image = ResourcesCompat.getDrawable(mBinding.root.context.resources, it.icon, null)
            val chip = Chip(requireContext())
            chip.text = it.description
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

            fragmentDetailPriceTv.text = String.format("$%,d",property.price)
            fragmentDetailPriceTv.visibility = property.priceVisibility

            fragmentDetailDescriptionTv.text = property.description
            fragmentDetailDescriptionTv.visibility = property.descriptionVisibility
            fragmentDetailDescriptionTitleTv.visibility = property.descriptionVisibility

            fragmentDetailSurfaceTv.text = String.format("%d sq ft",property.surface)
            fragmentDetailSurfaceTv.visibility = property.surfaceVisibility
            fragmentDetailSurfaceTitleTv.visibility = property.surfaceVisibility
            fragmentDetailSurfaceIv.visibility = property.surfaceVisibility

            fragmentDetailRoomsTv.text = property.roomsAmount.toString()
            fragmentDetailRoomsTv.visibility = property.roomsVisibility
            fragmentDetailRoomsIv.visibility = property.roomsVisibility
            fragmentDetailRoomsTitleTv.visibility = property.roomsVisibility

            fragmentDetailBathroomsTv.text = property.bathroomsAmount.toString()
            fragmentDetailBathroomsTv.visibility = property.bathroomsVisibility
            fragmentDetailBathroomsIv.visibility = property.bathroomsVisibility
            fragmentDetailBathroomsTitleTv.visibility = property.bathroomsVisibility

            fragmentDetailBedroomsTv.text = property.bedroomsAmount.toString()
            fragmentDetailBedroomsTv.visibility = property.bedroomsVisibility
            fragmentDetailBedroomsIv.visibility = property.bedroomsVisibility
            fragmentDetailBedroomsTitleTv.visibility = property.bedroomsVisibility

            fragmentDetailLocationTv.text = property.formattedAddress
            fragmentDetailLocationTv.visibility = property.addressVisibility
            fragmentDetailLocationIv.visibility = property.addressVisibility
            fragmentDetailLocationTitleTv.visibility = property.addressVisibility

            fragmentDetailMapIv.visibility = property.mapVisibility
            fragmentDetailMapCv.visibility = property.mapVisibility

            mBinding.fragmentDetailPointOfInterestCg.visibility = property.pointsOfInterestVisibility
            mBinding.fragmentDetailPointOfInterestIv.visibility = property.pointsOfInterestVisibility
            mBinding.fragmentDetailPointOfInterestTitleTv.visibility = property.pointsOfInterestVisibility

            fragmentDetailAvailableTv.text = formatCalendarToString(property.postDate)

            fragmentDetailAgentTv.text = property.agentName
            fragmentDetailAgentTv.visibility = property.agentVisibility
            fragmentDetailAgentIv.visibility = property.agentVisibility
            fragmentDetailAgentTitleTv.visibility = property.agentVisibility
        }

    }

    override fun onMediaClick(position: Int) {
        val intent = Intent(requireContext(), MediaViewerActivity::class.java)
        intent.putParcelableArrayListExtra(BUNDLE_KEY_MEDIA_LIST, mViewModel.propertyLiveData.value?.mediaList as ArrayList)
        intent.putExtra(BUNDLE_KEY_SELECTED_MEDIA_INDEX, position)
        startActivity(intent)
    }
}