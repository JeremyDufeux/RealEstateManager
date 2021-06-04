package com.openclassrooms.realestatemanager.ui.listFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.openclassrooms.realestatemanager.databinding.FragmentListItemBinding
import com.openclassrooms.realestatemanager.models.Property

class PropertyListAdapter(var mPropertyListener: OnPropertyListener) : RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder>() {

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

    inner class PropertyViewHolder(val mBinding : FragmentListItemBinding, val mPropertyListener: OnPropertyListener) : RecyclerView.ViewHolder(mBinding.root) {

        fun updateViewHolder(property: Property) {
            val context = mBinding.root.context

            Glide.with(context)
                .load(property.picturesUriList[0])
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBinding.fragmentListItemIv)

            mBinding.fragmentListItemCityTv.text = property.city
            mBinding.fragmentListItemPriceTv.text = property.price.toString()
            mBinding.fragmentListItemTypeTv.text = property.type.description

            mBinding.root.setOnClickListener { mPropertyListener.onPropertyClick(adapterPosition) }
        }
    }

    interface OnPropertyListener{
        fun onPropertyClick(position: Int)
    }
}