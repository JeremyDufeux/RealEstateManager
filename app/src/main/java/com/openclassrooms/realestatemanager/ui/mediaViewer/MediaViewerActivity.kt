package com.openclassrooms.realestatemanager.ui.mediaViewer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.databinding.ActivityMediaViewerBinding
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.utils.ZoomOutPageTransformer

const val BUNDLE_KEY_MEDIA_LIST = "BUNDLE_KEY_MEDIA_LIST"
const val BUNDLE_KEY_SELECTED_MEDIA_INDEX = "BUNDLE_KEY_SELECTED_MEDIA_INDEX"

class MediaViewerActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityMediaViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMediaViewerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        configureAdapter()
    }

    private fun configureAdapter() {
        val mediaList = intent?.extras?.getParcelableArrayList<MediaItem>(BUNDLE_KEY_MEDIA_LIST) as List<MediaItem>
        val index = intent?.extras?.getInt(BUNDLE_KEY_SELECTED_MEDIA_INDEX)!!

        mBinding.activityMediaViewerVp.apply {
            adapter = MediaViewerAdapter(this@MediaViewerActivity, mediaList)
            currentItem = index
            setPageTransformer(ZoomOutPageTransformer())
        }
    }
}