package com.openclassrooms.realestatemanager.ui.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.openclassrooms.realestatemanager.databinding.ActivityAddPropertyMediasItemBinding
import com.openclassrooms.realestatemanager.modules.GlideApp

class AddPropertyMediaListAdapter(var mMediaListener: MediaListener) : ListAdapter<Pair<String, String?>, AddPropertyMediaListAdapter.PropertyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val binding : ActivityAddPropertyMediasItemBinding = ActivityAddPropertyMediasItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PropertyViewHolder(binding, mMediaListener)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.updateViewHolder(getItem(position))
    }

    class PropertyViewHolder(private val mBinding : ActivityAddPropertyMediasItemBinding,
                             private val mMediaListener: MediaListener)
        : RecyclerView.ViewHolder(mBinding.root) {

        fun updateViewHolder(media: Pair<String, String?>) {
            val context = mBinding.root.context

            GlideApp.with(context)
                .load(media.first)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBinding.activityAddPropertyMediaItemIv)

            if(media.second != null) {
                mBinding.activityAddPropertyMediaItemTv.apply {
                    visibility = View.VISIBLE
                    text = media.second
                }
            }

            mBinding.root.setOnClickListener { mMediaListener.onMediaClick(adapterPosition) }
        }
    }

    interface MediaListener{
        fun onMediaClick(position: Int)
    }

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<Pair<String, String?>>(){
            override fun areItemsTheSame(oldItem: Pair<String, String?>, newItem: Pair<String, String?>): Boolean {
                return oldItem.first == newItem.first
            }

            override fun areContentsTheSame(oldItem: Pair<String, String?>, newItem: Pair<String, String?>): Boolean {
                return oldItem == newItem
            }
        }
    }
}