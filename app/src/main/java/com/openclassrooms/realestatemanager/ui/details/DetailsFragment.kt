package com.openclassrooms.realestatemanager.ui.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.openclassrooms.realestatemanager.databinding.FragmentDetailsBinding
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.modules.GlideApp
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat

private const val TAG = "DetailsFragment"

@AndroidEntryPoint
class DetailsFragment : Fragment(), MediaListAdapter.MediaListener {
    private val mViewModel: DetailsFragmentViewModel by viewModels()
    private lateinit var mBinding : FragmentDetailsBinding
    private val mMediaAdapter = MediaListAdapter(this)
    private val mPointOfInterestListAdapter = PointOfInterestListAdapter()

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

        mBinding.fragmentDetailPointOfInterestRv.adapter = mPointOfInterestListAdapter
        mBinding.fragmentDetailPointOfInterestRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
                fragmentDetailPointOfInterestRv.visibility = View.GONE
                fragmentDetailPointOfInterestIv.visibility = View.GONE
                fragmentDetailPointOfInterestTitleTv.visibility = View.GONE
            }
        } else {
            mPointOfInterestListAdapter.updateList(it.pointOfInterest)
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
            fragmentDetailAvailableTv.text = DateFormat.getDateInstance(DateFormat.LONG).format(it.saleDate.time)
            fragmentDetailAgentTv.text = it.agentName
        }

    }

    override fun onMediaClick(position: Int) {
        Log.d(TAG, "onMediaClick: $position")


    }

}