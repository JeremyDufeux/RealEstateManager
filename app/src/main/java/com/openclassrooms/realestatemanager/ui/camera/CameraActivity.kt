package com.openclassrooms.realestatemanager.ui.camera

import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityCameraBinding
import com.openclassrooms.realestatemanager.services.ImageSaver
import com.openclassrooms.realestatemanager.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

const val URI_RESULT_KEY = "URI_RESULT_KEY"

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {

    private val mViewModel: CameraActivityViewModel by viewModels()

    private lateinit var mBinding: ActivityCameraBinding

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.activityCameraCaptureBtn.setOnClickListener {
            mViewModel.takePicture(mCamera)
            showCheckButton()
        }
        mBinding.activityCameraGalleryBtn.setOnClickListener {
            openGallery()
        }
        mBinding.activityCameraCheckBtn.setOnClickListener {
            mViewModel.savePicture()
        }
        mBinding.activityCameraCancelBtn.setOnClickListener {
            mCamera?.startPreview()
            hideCheckButton()
        }

        configureViewModel()
    }

    private fun configureViewModel() {
        mViewModel.startOrientationService()
        mViewModel.fileStateFlow.observe(this, fileStateObserver)
        mViewModel.rotationLiveData.observe(this, rotationObserver)
    }

    private val rotationObserver = Observer<Float?> { rotation ->
        if(rotation != null) {
            mBinding.activityCameraGalleryBtn.animate().rotation(rotation).setDuration(500).start()
            mBinding.activityCameraCheckBtn.animate().rotation(rotation).setDuration(500).start()
        }
    }

    private val fileStateObserver = Observer<ImageSaver.FileState> { state ->
        when(state){
            is ImageSaver.FileState.Success -> finishActivityOk(state.file.absolutePath)
            is ImageSaver.FileState.Error -> {
                showToast(this@CameraActivity, R.string.error_saving_file)
                finishActivityError()
            }
            is ImageSaver.FileState.Empty -> {}
        }
    }

    private fun showCheckButton() {
        mBinding.activityCameraCheckBtn.visibility = View.VISIBLE
        mBinding.activityCameraCancelBtn.visibility = View.VISIBLE
        mBinding.activityCameraCaptureBtn.visibility = View.GONE
        mBinding.activityCameraGalleryBtn.visibility = View.GONE
    }

    private fun hideCheckButton() {
        mBinding.activityCameraCheckBtn.visibility = View.GONE
        mBinding.activityCameraCancelBtn.visibility = View.GONE
        mBinding.activityCameraCaptureBtn.visibility = View.VISIBLE
        mBinding.activityCameraGalleryBtn.visibility = View.VISIBLE
    }

    private fun configureCamera(){
        mCamera = Camera.open()

        mPreview = mCamera?.let {
            CameraPreview(this, it)
        }
        mPreview?.also {
            val preview: FrameLayout = mBinding.activityCameraFl
            preview.addView(it)
        }
    }

    private fun openGallery(){
        resultLauncher.launch("image/*")
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            finishActivityOk(uri.toString())
        }
    }

    private fun finishActivityOk(file: String ){
        val data = Intent()
        data.putExtra(URI_RESULT_KEY, file)
        setResult(RESULT_OK, data)
        finish()
    }

    private fun finishActivityError(){
        val data = Intent()
        setResult(RESULT_CANCELED, data)
        finish()
    }

    override fun onResume() {
        super.onResume()
        mViewModel.enableOrientationService()
        try {
            configureCamera()
        } catch (e: RuntimeException) {
            Timber.e("Error configureCamera : ${e.message.toString()}")
            showToast(this, R.string.error_opening_camera)
            finishActivityError()
        }
    }

    override fun onPause() {
        super.onPause()
        mViewModel.disableOrientationService()
        releaseCamera()
    }

    private fun releaseCamera() {
        mCamera?.stopPreview()
        mCamera?.release()
        mCamera = null
        mPreview?.holder?.removeCallback(mPreview)
    }
}