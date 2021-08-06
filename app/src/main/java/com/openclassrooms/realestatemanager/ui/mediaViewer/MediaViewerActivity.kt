package com.openclassrooms.realestatemanager.ui.mediaViewer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMediaViewerBinding
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.utils.ZoomOutPageTransformer
import com.openclassrooms.realestatemanager.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

const val BUNDLE_KEY_EDIT_MODE = "BUNDLE_KEY_EDIT_MODE"
const val BUNDLE_KEY_MEDIA_LIST = "BUNDLE_KEY_MEDIA_LIST"
const val BUNDLE_KEY_SELECTED_MEDIA_INDEX = "BUNDLE_KEY_SELECTED_MEDIA_INDEX"

const val MEDIA_VIEWER_RESULT_MEDIA_KEY = "MEDIA_VIEWER_RESULT_MEDIA_KEY"

@AndroidEntryPoint
class MediaViewerActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMediaViewerBinding
    private lateinit var mMediaList: MutableList<MediaItem>
    private var mMediaIndex = 0
    private var mEditMode =  false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMediaViewerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.activityMediaViewerCloseBtn.setOnClickListener { finishActivityWithoutFile() }

        mMediaList = intent?.extras?.getParcelableArrayList<MediaItem>(BUNDLE_KEY_MEDIA_LIST) as MutableList<MediaItem>
        mMediaIndex = intent?.extras?.getInt(BUNDLE_KEY_SELECTED_MEDIA_INDEX)!!
        mEditMode = intent?.extras?.getBoolean(BUNDLE_KEY_EDIT_MODE)!!

        if(mEditMode){
            mBinding.apply {
                activityMediaViewerCheckBtn.visibility = View.VISIBLE
                activityMediaViewerDescriptionEt.visibility = View.VISIBLE
                activityMediaViewerDescriptionEt.setText(mMediaList[0].description)

                activityMediaViewerDescriptionEt.setOnEditorActionListener { _, actionId, _ ->
                    if(actionId == EditorInfo.IME_ACTION_SEND){
                        checkDescriptionEditText()
                    }
                    return@setOnEditorActionListener true
                }

                activityMediaViewerCheckBtn.setOnClickListener {
                    checkDescriptionEditText()
                }
            }
        }

        configureAdapter()
    }

    private fun checkDescriptionEditText(){
        if(mBinding.activityMediaViewerDescriptionEt.text.isNullOrEmpty()){
            showToast(this, R.string.please_add_a_description)
        } else {
            finishActivityWithFile()
        }
    }

    private fun configureAdapter() {
        mBinding.activityMediaViewerVp.apply {
            adapter = MediaViewerAdapter(this@MediaViewerActivity, mMediaList, mEditMode)
            currentItem = mMediaIndex
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