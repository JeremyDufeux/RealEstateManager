package com.openclassrooms.realestatemanager.ui.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.openclassrooms.realestatemanager.databinding.ActivityAddPropertyMediasItemBinding
import com.openclassrooms.realestatemanager.models.FileType
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.modules.GlideApp

class AddPropertyMediaListAdapter(private var mMediaListener: MediaListener) : ListAdapter<MediaItem, AddPropertyMediaListAdapter.PropertyViewHolder>(DiffCallback) {

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

        fun updateViewHolder(media: MediaItem) {  // TODO Add video icon + delete icon
            val context = mBinding.root.context

            GlideApp.with(context)
                .load(media.url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.errorPlaceholder() // TODO
                .into(mBinding.activityAddPropertyMediaItemIv)

            if(!media.description.isNullOrEmpty()) {
                mBinding.activityAddPropertyMediaItemTv.apply {
                    visibility = View.VISIBLE
                    text = media.description
                }
            }

            if(media.fileType == FileType.VIDEO){
                mBinding.activityAddPropertyVideoIv.visibility = View.VISIBLE
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
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}