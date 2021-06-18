package com.openclassrooms.realestatemanager.ui.details

import android.os.Bundle
import android.util.Log
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
import com.openclassrooms.realestatemanager.extensions.formatCalendarToString
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.modules.GlideApp
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "DetailsFragment"

@AndroidEntryPoint
class DetailsFragment : Fragment(), MediaListAdapter.MediaListener {
    private val mViewModel: DetailsFragmentViewModel by viewModels()
    private lateinit var mBinding : FragmentDetailsBinding
    private val mMediaAdapter = MediaListAdapter(this)

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

    private val propertyObserver = Observer<Property> {

        if(it.picturesUriList.isEmpty()){
            mBinding.apply {
                fragmentDetailMediaRv.visibility = View.GONE
                fragmentDetailMediaTitleTv.visibility = View.GONE
            }
        } else {
            mMediaAdapter.updateList(it.picturesUriList)
        }

        if(it.pointOfInterest.isEmpty()){
            mBinding.apply {
                fragmentDetailPointOfInterestCg.visibility = View.GONE
                fragmentDetailPointOfInterestIv.visibility = View.GONE
                fragmentDetailPointOfInterestTitleTv.visibility = View.GONE
            }
        } else {
            it.pointOfInterest.map {
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
            .load(it.mapPictureUrl)
            .centerCrop()
            .timeout(2000)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mBinding.fragmentDetailMapIv)

        mBinding.apply {
            fragmentDetailTypeTv.text = it.type.description
            fragmentDetailPriceTv.text = String.format("$%,d",it.price)
            fragmentDetailDescriptionTv.text = it.description
            fragmentDetailSurfaceTv.text = it.surface
            fragmentDetailRoomsTv.text = it.roomsAmount.toString()
            fragmentDetailBathroomsTv.text = it.bathroomsAmount.toString()
            fragmentDetailBedroomsTv.text = it.bedroomsAmount.toString()
            val address = "${it.address}\n${it.city}\n${it.postalCode}\n${it.country}"
            fragmentDetailLocationTv.text = address
            fragmentDetailAvailableTv.text = formatCalendarToString(it.saleDate.time)
            fragmentDetailAgentTv.text = it.agentName
        }

    }

    override fun onMediaClick(position: Int) {
        Log.d(TAG, "onMediaClick: $position")


    }

}