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
import com.openclassrooms.realestatemanager.models.FileState
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

    enum class CameraMode{
        PHOTO, VIDEO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        configureUi()
        configureViewModel()
    }

    private fun configureUi(){
        mBinding.apply {
            activityCameraCapturePhotoBtn.setOnClickListener {
                takePicture()
                showCheckButton()
            }
            activityCameraCaptureVideoBtn.setOnClickListener {
                toggleCaptureVideoButton()
            }
            activityCameraGalleryBtn.setOnClickListener {
                openGallery()
            }
            activityCameraCheckBtn.setOnClickListener {
                savePicture()
            }
            activityCameraCancelBtn.setOnClickListener {
                mCamera?.startPreview()
                hideCheckButton()
            }
            activityCameraVideoBtn.setOnClickListener {
                mViewModel.cameraMode = CameraMode.VIDEO
                displayActualMode()
            }
            activityCameraPhotoBtn.setOnClickListener {
                mViewModel.cameraMode = CameraMode.PHOTO
                displayActualMode()
            }
            activityCameraFl.setOnClickListener {
                mCamera?.autoFocus(null)
            }
        }
    }

    private fun toggleCaptureVideoButton(){
        mViewModel.recording = !mViewModel.recording
        if(mViewModel.recording){
            startRecording()
            mBinding.activityCameraCaptureVideoBtn.setImageResource(R.drawable.ic_shutter_video_recording)
        } else{
            stopRecording()
            mBinding.activityCameraCaptureVideoBtn.setImageResource(R.drawable.ic_shutter_video_normal)
        }
    }

    private fun displayActualMode(){
        mBinding.apply {
            when (mViewModel.cameraMode) {
                CameraMode.PHOTO -> {
                    activityCameraVideoBtn.visibility = View.VISIBLE
                    activityCameraPhotoBtn.visibility = View.GONE
                    activityCameraCaptureVideoBtn.visibility = View.GONE
                    activityCameraCapturePhotoBtn.visibility = View.VISIBLE
                }
                CameraMode.VIDEO -> {
                    activityCameraVideoBtn.visibility = View.GONE
                    activityCameraPhotoBtn.visibility = View.VISIBLE
                    activityCameraCaptureVideoBtn.visibility = View.VISIBLE
                    activityCameraCapturePhotoBtn.visibility = View.GONE
                }
            }
        }
    }

    private fun configureViewModel() {
        mViewModel.startOrientationService()
        mViewModel.pictureFileStateFlow.observe(this, fileStateObserver)
        mViewModel.rotationLiveData.observe(this, rotationObserver)
    }

    private val rotationObserver = Observer<Float?> { rotation ->
        if(rotation != null) {
            mBinding.apply {
                activityCameraGalleryBtn.animate().rotation(rotation).setDuration(500).start()
                activityCameraCheckBtn.animate().rotation(rotation).setDuration(500).start()
                activityCameraVideoBtn.animate().rotation(rotation).setDuration(500).start()
                activityCameraPhotoBtn.animate().rotation(rotation).setDuration(500).start()
            }
        }
    }

    private val fileStateObserver = Observer<FileState> { state ->
        when(state){
            is FileState.Success -> finishActivityOk(state.file.absolutePath)
            is FileState.Error -> {
                showToast(this@CameraActivity, R.string.error_saving_file)
                finishActivityError()
            }
            is FileState.Empty -> {}
        }
    }

    private fun showCheckButton() {
        mBinding.apply {
            activityCameraCheckBtn.visibility = View.VISIBLE
            activityCameraCancelBtn.visibility = View.VISIBLE
            activityCameraCapturePhotoBtn.visibility = View.GONE
            activityCameraCaptureVideoBtn.visibility = View.GONE
            activityCameraGalleryBtn.visibility = View.GONE
            activityCameraVideoBtn.visibility = View.GONE
            activityCameraPhotoBtn.visibility = View.GONE
        }
    }

    private fun hideCheckButton() {
        mBinding.apply {
            activityCameraCheckBtn.visibility = View.GONE
            activityCameraCancelBtn.visibility = View.GONE
            activityCameraCapturePhotoBtn.visibility = View.VISIBLE
            activityCameraGalleryBtn.visibility = View.VISIBLE
        }
        displayActualMode()
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

    private fun takePicture() {
        mCamera?.autoFocus(null)
        mCamera?.takePicture(null, null, mViewModel.mPictureSaver)
    }

    private fun savePicture() {
        mViewModel.mPictureSaver.savePicture()
    }

    private fun startRecording() {
        mCamera?.autoFocus(null)
        mCamera?.unlock()
        mCamera?.let { mViewModel.mVideoRecorder.startRecording(it) }
    }

    private fun stopRecording() {
        mViewModel.mVideoRecorder.stopRecording()
        configureCamera()
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