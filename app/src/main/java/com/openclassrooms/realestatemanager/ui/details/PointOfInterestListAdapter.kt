package com.openclassrooms.realestatemanager.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.FragmentDetailsPointOfInterestItemBinding
import com.openclassrooms.realestatemanager.models.PointsOfInterest

class PointOfInterestListAdapter() : RecyclerView.Adapter<PointOfInterestListAdapter.PropertyViewHolder>() {

    private var mPointOfInterestList : List<PointsOfInterest> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val binding : FragmentDetailsPointOfInterestItemBinding = FragmentDetailsPointOfInterestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PropertyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.updateViewHolder(mPointOfInterestList[position])
    }

    override fun getItemCount(): Int {
        return mPointOfInterestList.size
    }

    fun updateList(list : List<PointsOfInterest>){
        mPointOfInterestList = list
    }

    inner class PropertyViewHolder(private val mBinding : FragmentDetailsPointOfInterestItemBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun updateViewHolder(pointsOfInterest: PointsOfInterest) {
            val image = ResourcesCompat.getDrawable(mBinding.root.context.resources, pointsOfInterest.icon, null)
            mBinding.fragmentDetailPointOfInterestItemIv.setImageDrawable(image)
            mBinding.fragmentDetailPointOfInterestItemTv.text = pointsOfInterest.description
        }
    }
}