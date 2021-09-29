package com.openclassrooms.realestatemanager.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentListItemBinding
import com.openclassrooms.realestatemanager.models.ui.PropertyUiListView

class PropertyListAdapter(private var mPropertyListener: PropertyListener) : RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder>() {

    private var mSelectedItem = 0

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

    fun selectProperty(position: Int) {
        val previousItem = mSelectedItem
        mSelectedItem = position

        notifyItemChanged(previousItem)
        notifyItemChanged(position)
    }

    fun updateList(list : List<PropertyUiListView>){
        mPropertyList.submitList(list)
    }

    inner class PropertyViewHolder(private val mBinding : FragmentListItemBinding,
                                   private val mPropertyListener: PropertyListener
                                   ) : RecyclerView.ViewHolder(mBinding.root) {

        fun updateViewHolder(property: PropertyUiListView, position: Int) {
            val context = mBinding.root.context

            mBinding.fragmentListItemPcv.setSold(property.sold)
            mBinding.fragmentListItemPcv.setPropertyPicture(property.pictureUrl)
            mBinding.fragmentListItemCityTv.text = property.city
            mBinding.fragmentListItemPriceTv.text = property.priceString
            mBinding.fragmentListItemPriceTv.visibility = property.priceVisibility
            mBinding.fragmentListItemTypeTv.text = context.getString(property.type.description)

            mBinding.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            mBinding.fragmentListItemPriceTv.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))

            if(mSelectedItem == position && context.resources.getBoolean(R.bool.isTabletLand)){
                mBinding.root.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                mBinding.fragmentListItemPriceTv.setTextColor(ContextCompat.getColor(context, R.color.white))
            }

            mBinding.root.setOnClickListener {
                mPropertyListener.onPropertyClick(bindingAdapterPosition)
                selectProperty(bindingAdapterPosition)
            }
        }
    }

    interface PropertyListener{
        fun onPropertyClick(position: Int)
    }
}