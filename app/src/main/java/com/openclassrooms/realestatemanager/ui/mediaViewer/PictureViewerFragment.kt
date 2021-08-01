package com.openclassrooms.realestatemanager.ui.mediaViewer

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.openclassrooms.realestatemanager.databinding.FragmentPrictureViewerBinding


class PictureViewerFragment : Fragment() {
    private lateinit var mBinding: FragmentPrictureViewerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPrictureViewerBinding.inflate(inflater)

        val url = arguments?.getString(BUNDLE_KEY_MEDIA_URL)

        Glide.with(requireActivity())
            .asBitmap()
            .load(url)
            .into(bitmapTarget())

        arguments?.getString(BUNDLE_KEY_MEDIA_DESCRIPTION) ?.let {
            if (it.isNotBlank() && it.isNotEmpty()) {
                mBinding.pictureViewerFragmentTv.text = it
                mBinding.pictureViewerFragmentTv.visibility = View.VISIBLE
            }
        }

        return mBinding.root
    }

    private fun bitmapTarget() = object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            mBinding.pictureViewerFragmentPv.setImageBitmap(resource)
        }

        override fun onLoadCleared(placeholder: Drawable?) {
        }
    }
}