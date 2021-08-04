package com.openclassrooms.realestatemanager.ui.mediaViewer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.databinding.ActivityMediaViewerBinding
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.utils.ZoomOutPageTransformer
import dagger.hilt.android.AndroidEntryPoint

const val BUNDLE_KEY_EDIT_MODE = "BUNDLE_KEY_EDIT_MODE"
const val BUNDLE_KEY_MEDIA_LIST = "BUNDLE_KEY_MEDIA_LIST"
const val BUNDLE_KEY_SELECTED_MEDIA_INDEX = "BUNDLE_KEY_SELECTED_MEDIA_INDEX"

const val MEDIA_VIEWER_RESULT_MEDIA_KEY = "MEDIA_VIEWER_RESULT_MEDIA_KEY"

@AndroidEntryPoint
class MediaViewerActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityMediaViewerBinding
    private var mMediaList = mutableListOf<MediaItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMediaViewerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.activityMediaViewerCloseBtn.setOnClickListener { finishActivityWithoutFile() }

        if(intent?.extras?.getBoolean(BUNDLE_KEY_EDIT_MODE) != null && intent?.extras?.getBoolean(BUNDLE_KEY_EDIT_MODE) == true){
            mBinding.apply {
                activityMediaViewerCheckBtn.visibility = View.VISIBLE
                activityMediaViewerDescriptionEt.visibility = View.VISIBLE

                activityMediaViewerDescriptionEt.setOnEditorActionListener { _, actionId, _ ->
                    if(actionId == EditorInfo.IME_ACTION_SEND){
                        finishActivityWithFile()
                    }
                    return@setOnEditorActionListener true
                }

                activityMediaViewerCheckBtn.setOnClickListener {
                    finishActivityWithFile()
                }
            }
        }

        configureAdapter()
    }

    private fun configureAdapter() {
        mMediaList = intent?.extras?.getParcelableArrayList<MediaItem>(BUNDLE_KEY_MEDIA_LIST) as MutableList<MediaItem>
        val index = intent?.extras?.getInt(BUNDLE_KEY_SELECTED_MEDIA_INDEX)!!

        mBinding.activityMediaViewerVp.apply {
            adapter = MediaViewerAdapter(this@MediaViewerActivity, mMediaList)
            currentItem = index
            setPageTransformer(ZoomOutPageTransformer())
        }
    }

    private fun finishActivityWithFile() {
        val data = Intent()
        mMediaList[0].description = mBinding.activityMediaViewerDescriptionEt.text.toString()
        data.putExtra(MEDIA_VIEWER_RESULT_MEDIA_KEY, mMediaList[0])

        setResult(RESULT_OK, data)
        finish()
    }

    private fun finishActivityWithoutFile(){
        val data = Intent()
        setResult(RESULT_CANCELED, data)
        finish()
    }
}