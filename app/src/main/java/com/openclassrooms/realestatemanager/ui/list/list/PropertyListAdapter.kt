package com.openclassrooms.realestatemanager.ui.list.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentListItemBinding
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.modules.GlideApp

class PropertyListAdapter(var mPropertyListener: PropertyListener) : RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Property>() {
        override fun areItemsTheSame(oldItem: Property, newItem: Property): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Property, newItem: Property): Boolean {
            return oldItem.description == newItem.description
        }
    }

    private val mPropertyList = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val binding : FragmentListItemBinding = FragmentListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PropertyViewHolder(binding, mPropertyListener)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.updateViewHolder(mPropertyList.currentList[position])
    }

    override fun getItemCount(): Int {
        return mPropertyList.currentList.size
    }

    fun updateList(list : List<Property>){
        mPropertyList.submitList(list)
    }

    inner class PropertyViewHolder(private val mBinding : FragmentListItemBinding,
                                   private val mPropertyListener: PropertyListener
                                   ) : RecyclerView.ViewHolder(mBinding.root) {

        fun updateViewHolder(property: Property) {
            val context = mBinding.root.context

            if (property.mediaUriList.isNotEmpty()) {
                GlideApp.with(context)
                    .load(property.mediaUriList[0].first)
                    .centerCrop()
                    .timeout(2000)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mBinding.fragmentListItemIv)
            } else{
                GlideApp.with(context)
                    .load(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_building, null))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mBinding.fragmentListItemIv)
            }

            mBinding.fragmentListItemCityTv.text = property.city
            mBinding.fragmentListItemPriceTv.text = String.format("$%,d", property.price)
            mBinding.fragmentListItemTypeTv.text = property.type.description

            mBinding.root.setOnClickListener { mPropertyListener.onPropertyClick(adapterPosition) }
        }
    }

    interface PropertyListener{
        fun onPropertyClick(position: Int)
    }
}