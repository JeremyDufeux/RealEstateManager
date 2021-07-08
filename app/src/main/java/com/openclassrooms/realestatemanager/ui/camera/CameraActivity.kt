package com.openclassrooms.realestatemanager.ui.camera

import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityCameraBinding
import com.openclassrooms.realestatemanager.services.ImageSaver
import com.openclassrooms.realestatemanager.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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
                mBinding.activityCameraCaptureBtn.isEnabled = false
                mBinding.activityCameraCaptureBtn.setImageResource(R.drawable.ic_shutter_pressed)
                mViewModel.takePicture(mCamera)
        }
        mBinding.activityCameraGalleryBtn.setOnClickListener {
            openGallery()
        }

        configureViewModel()
    }

    private fun configureViewModel() {
        mViewModel.startOrientationService()

        lifecycleScope.launchWhenStarted {
            mViewModel.fileStateFlow.collect { state ->
                when(state){
                    is ImageSaver.FileState.Success -> finishActivityOk(state.file.absolutePath)
                    is ImageSaver.FileState.Error -> {
                        showToast(this@CameraActivity, R.string.error_saving_file)
                        finishActivityError()
                    }
                    is ImageSaver.FileState.Empty -> {}
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            configureCamera()
        } catch (e: RuntimeException) {
            Timber.e("Error configureCamera : ${e.message.toString()}")
            showToast(this, R.string.error_opening_camera)
            finishActivityError()
        }
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

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri ->
        finishActivityOk(uri.toString())
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

    override fun onPause() {
        super.onPause()
        mViewModel.disableOrientationService()
        releaseCamera()
    }

    private fun releaseCamera() {
        mCamera?.stopPreview()
        mCamera?.release()
        mCamera = null
    }
}