package com.openclassrooms.realestatemanager.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentDetailsBinding
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.modules.GlideApp
import com.openclassrooms.realestatemanager.utils.formatCalendarToString
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

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

        mMediaAdapter.updateList(property.mediaList)

        if(property.pointOfInterest.isEmpty()){
            mBinding.apply {
                fragmentDetailPointOfInterestCg.visibility = View.GONE
                fragmentDetailPointOfInterestIv.visibility = View.GONE
                fragmentDetailPointOfInterestTitleTv.visibility = View.GONE
            }
        } else {
            property.pointOfInterest.map {
                val image = ResourcesCompat.getDrawable(mBinding.root.context.resources, it.icon, null)
                val chip = Chip(requireContext())
                chip.text = it.description
                chip.tag = it
                chip.chipIcon = image
                chip.setChipIconTintResource( R.color.colorAccent)
                chip.isClickable = false
                mBinding.fragmentDetailPointOfInterestCg.addView(chip)
            }
        }

        GlideApp.with(this)
            .load(property.mapPictureUrl)
            .centerCrop()
            .timeout(2000)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mBinding.fragmentDetailMapIv)

        mBinding.apply {
            fragmentDetailTypeTv.text = property.type.description
            fragmentDetailPriceTv.text = String.format("$%,d",property.price)
            fragmentDetailDescriptionTv.text = property.description
            fragmentDetailSurfaceTv.text = property.surface
            fragmentDetailRoomsTv.text = property.roomsAmount.toString()
            fragmentDetailBathroomsTv.text = property.bathroomsAmount.toString()
            fragmentDetailBedroomsTv.text = property.bedroomsAmount.toString()
            val address = "${property.address}\n${property.city}\n${property.postalCode}\n${property.country}"
            fragmentDetailLocationTv.text = address
            fragmentDetailAvailableTv.text = formatCalendarToString(property.saleDate)
            fragmentDetailAgentTv.text = property.agentName
        }

    }

    override fun onMediaClick(position: Int) {
        Timber.d("Debug onMediaClick: $position")
    }

}