package com.openclassrooms.realestatemanager.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentDetailsMediaItemBinding
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.modules.GlideApp

class DetailsMediaListAdapter(private var mMediaListener: MediaListener) : ListAdapter<MediaItem, DetailsMediaListAdapter.PropertyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val binding : FragmentDetailsMediaItemBinding = FragmentDetailsMediaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PropertyViewHolder(binding, mMediaListener)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.updateViewHolder(getItem(position))
    }

    class PropertyViewHolder(private val mBinding : FragmentDetailsMediaItemBinding,
                             private val mMediaListener: MediaListener)
        : RecyclerView.ViewHolder(mBinding.root) {

        fun updateViewHolder(media: MediaItem) {
            val context = mBinding.root.context

            GlideApp.with(context)
                .load(media.url)
                .centerCrop()
                .timeout(2000)
                .error(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_building, null))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBinding.fragmentDetailMediaItemIv)

            if(!media.description.isNullOrEmpty()) {
                mBinding.fragmentDetailMediaItemTv.apply {
                    visibility = View.VISIBLE
                    text = media.description
                }
            }

            if(media.fileType == FileType.VIDEO){
                mBinding.fragmentDetailVideoIv.visibility = View.VISIBLE
            }

            mBinding.root.setOnClickListener { mMediaListener.onMediaClick(adapterPosition) }
        }
    }

    interface MediaListener{
        fun onMediaClick(position: Int)
    }

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<MediaItem>(){
            override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}