package com.openclassrooms.realestatemanager.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.openclassrooms.realestatemanager.databinding.FragmentDetailsMediaItemBinding
import com.openclassrooms.realestatemanager.modules.GlideApp

class DetailsMediaListAdapter(var mMediaListener: MediaListener) : RecyclerView.Adapter<DetailsMediaListAdapter.PropertyViewHolder>() {

    private var mMediaList : List<Pair<String, String?>> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val binding : FragmentDetailsMediaItemBinding = FragmentDetailsMediaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PropertyViewHolder(binding, mMediaListener)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.updateViewHolder(mMediaList[position])
    }

    override fun getItemCount(): Int {
        return mMediaList.size
    }

    fun updateList(list : List<Pair<String, String?>>){
        mMediaList = list
    }

    inner class PropertyViewHolder(private val mBinding : FragmentDetailsMediaItemBinding, val mMediaListener: MediaListener) : RecyclerView.ViewHolder(mBinding.root) {

        fun updateViewHolder(media: Pair<String, String?>) {
            val context = mBinding.root.context

            GlideApp.with(context)
                .load(media.first)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBinding.fragmentDetailMediaItemIv)

            mBinding.fragmentDetailMediaItemTv.text = media.second

            mBinding.root.setOnClickListener { mMediaListener.onMediaClick(adapterPosition) }
        }
    }

    interface MediaListener{
        fun onMediaClick(position: Int)
    }
}