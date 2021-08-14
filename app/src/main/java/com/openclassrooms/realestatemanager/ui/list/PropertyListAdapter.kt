package com.openclassrooms.realestatemanager.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentListItemBinding
import com.openclassrooms.realestatemanager.models.ui.PropertyUiListView

class PropertyListAdapter(private var mPropertyListener: PropertyListener) : RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder>() {

    private var mSelectedItem = -1

    private val differCallback = object : DiffUtil.ItemCallback<PropertyUiListView>() {
        override fun areItemsTheSame(oldItem: PropertyUiListView, newItem: PropertyUiListView): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PropertyUiListView, newItem: PropertyUiListView): Boolean {
            return oldItem == newItem
        }
    }

    private val mPropertyList = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val binding : FragmentListItemBinding = FragmentListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PropertyViewHolder(binding, mPropertyListener)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.updateViewHolder(mPropertyList.currentList[position], position)
    }

    override fun getItemCount(): Int {
        return mPropertyList.currentList.size
    }

    fun updateList(list : List<PropertyUiListView>){
        mPropertyList.submitList(list)
    }

    inner class PropertyViewHolder(private val mBinding : FragmentListItemBinding,
                                   private val mPropertyListener: PropertyListener
                                   ) : RecyclerView.ViewHolder(mBinding.root) {

        fun updateViewHolder(property: PropertyUiListView, position: Int) {
            val context = mBinding.root.context

            Glide.with(context)
                .load(property.pictureUrl)
                .centerCrop()
                .timeout(2000)
                .error(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_building, null))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBinding.fragmentListItemIv)

            mBinding.fragmentListItemSoldTv.visibility = property.soldVisibility
            mBinding.fragmentListItemCityTv.text = property.city
            mBinding.fragmentListItemPriceTv.text = property.priceString
            mBinding.fragmentListItemPriceTv.visibility = property.priceVisibility
            mBinding.fragmentListItemTypeTv.text = property.type.description

            mBinding.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            mBinding.fragmentListItemPriceTv.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))

            if(mSelectedItem == position && context.resources.getBoolean(R.bool.isTablet)){
                mBinding.root.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                mBinding.fragmentListItemPriceTv.setTextColor(ContextCompat.getColor(context, R.color.white))
            }

            mBinding.root.setOnClickListener {
                mPropertyListener.onPropertyClick(adapterPosition)

                val previousItem = mSelectedItem
                mSelectedItem = adapterPosition

                notifyItemChanged(previousItem)
                notifyItemChanged(adapterPosition)
            }
        }
    }

    interface PropertyListener{
        fun onPropertyClick(position: Int)
    }
}