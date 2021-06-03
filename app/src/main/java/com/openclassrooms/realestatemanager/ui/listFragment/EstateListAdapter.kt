package com.openclassrooms.realestatemanager.ui.listFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.openclassrooms.realestatemanager.databinding.FragmentListItemBinding
import com.openclassrooms.realestatemanager.models.Estate

class EstateListAdapter(var mEstateListener: OnEstateListener) : RecyclerView.Adapter<EstateListAdapter.EstateViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Estate>() {
        override fun areItemsTheSame(oldItem: Estate, newItem: Estate): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Estate, newItem: Estate): Boolean {
            return oldItem.description == newItem.description
        }
    }

    private val mEstateList = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstateViewHolder {
        val binding : FragmentListItemBinding = FragmentListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EstateViewHolder(binding, mEstateListener)
    }

    override fun onBindViewHolder(holder: EstateViewHolder, position: Int) {
        holder.updateViewHolder(mEstateList.currentList[position])
    }

    override fun getItemCount(): Int {
        return mEstateList.currentList.size
    }

    fun updateList(list : List<Estate>){
        mEstateList.submitList(list)
    }

    inner class EstateViewHolder(val mBinding : FragmentListItemBinding, val mEstateListener: OnEstateListener) : RecyclerView.ViewHolder(mBinding.root) {

        fun updateViewHolder(estate: Estate) {
            val context = mBinding.root.context

            Glide.with(context)
                .load(estate.picturesUriList[0])
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBinding.fragmentListItemIv)

            mBinding.fragmentListItemCityTv.text = estate.city
            mBinding.fragmentListItemPriceTv.text = estate.price.toString()
            mBinding.fragmentListItemTypeTv.text = estate.type.description

            mBinding.root.setOnClickListener { mEstateListener.onEstateClick(adapterPosition) }
        }
    }

    interface OnEstateListener{
        fun onEstateClick(position: Int)
    }
}