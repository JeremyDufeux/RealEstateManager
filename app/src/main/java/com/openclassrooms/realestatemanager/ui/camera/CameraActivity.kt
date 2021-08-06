package com.openclassrooms.realestatemanager.ui.camera

import android.content.ContentResolver
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityCameraBinding
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.models.sealedClasses.FileState
import com.openclassrooms.realestatemanager.ui.camera.CameraActivityViewModel.CameraMode.*
import com.openclassrooms.realestatemanager.ui.mediaViewer.*
import com.openclassrooms.realestatemanager.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

const val CAMERA_RESULT_MEDIA_KEY = "CAMERA_RESULT_MEDIA_KEY"

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {

    private val mViewModel: CameraActivityViewModel by viewModels()

    private lateinit var mBinding: ActivityCameraBinding

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null

    private lateinit var mFileState: FileState.Success

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        configureViewModel()
        configureUi()
    }

    private fun configureViewModel() {
        mViewModel.rotationLiveData.observe(this, rotationObserver)
        mViewModel.fileLiveData.observe(this, fileStateObserver)
    }

    private val rotationObserver = Observer<Float?> { rotation ->
        if(rotation != null) {
            mBinding.apply {
                activityCameraGalleryBtn.animate().rotation(rotation).setDuration(500).start()
                activityCameraVideoBtn.animate().rotation(rotation).setDuration(500).start()
                activityCameraPhotoBtn.animate().rotation(rotation).setDuration(500).start()
            }
        }
    }

    private val fileStateObserver = Observer<FileState> { state ->
        when(state){
            is FileState.Success -> {
                mFileState = state

                releaseCamera()
                startMediaViewerActivity()
            }
            is FileState.Error -> {
                showToast(this@CameraActivity, R.string.error_saving_file)
                finishActivityWithoutFile()
            }
            is FileState.Empty -> {}
        }
    }

    private fun startMediaViewerActivity() {
        val intent = Intent(this, MediaViewerActivity::class.java)
        intent.putParcelableArrayListExtra(BUNDLE_KEY_MEDIA_LIST, arrayListOf(mFileState.mediaItem))
        intent.putExtra(BUNDLE_KEY_SELECTED_MEDIA_INDEX, 0)
        intent.putExtra(BUNDLE_KEY_EDIT_MODE, true)
        openMediaViewerLauncher.launch(intent)
    }

    private fun configureUi(){
        mBinding.apply {
            activityCameraCapturePhotoBtn.setOnClickListener {
                takePicture()
            }
            activityCameraCaptureVideoBtn.setOnClickListener {
                toggleCaptureVideoButton()
            }
            activityCameraGalleryBtn.setOnClickListener {
                openGallery()
            }
            activityCameraVideoBtn.setOnClickListener {
                mViewModel.cameraMode = VIDEO
                displayActualMode()
            }
            activityCameraPhotoBtn.setOnClickListener {
                mViewModel.cameraMode = PHOTO
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
                PHOTO -> {
                    activityCameraVideoBtn.visibility = View.VISIBLE
                    activityCameraPhotoBtn.visibility = View.GONE
                    activityCameraCaptureVideoBtn.visibility = View.GONE
                    activityCameraCapturePhotoBtn.visibility = View.VISIBLE
                }
                VIDEO -> {
                    activityCameraVideoBtn.visibility = View.GONE
                    activityCameraPhotoBtn.visibility = View.VISIBLE
                    activityCameraCaptureVideoBtn.visibility = View.VISIBLE
                    activityCameraCapturePhotoBtn.visibility = View.GONE
                }
            }
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

    private fun takePicture() {
        mCamera?.autoFocus(null)
        mCamera?.takePicture(null, null, mViewModel.getPictureSaver())
    }

    private fun startRecording() {
        mCamera?.autoFocus(null)
        mCamera?.unlock()
        mCamera?.let { mViewModel.startRecording(it) }
    }

    private fun stopRecording() {
        mViewModel.stopRecording()
        configureCamera()
    }

    private fun openGallery(){
        openGalleryLauncher.launch(arrayOf("image/*", "video/*"))
    }

    private var openGalleryLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                val perms = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, perms)
            }
            val mediaItem = MediaItem(UUID.randomUUID().toString(), "", uri.toString(), "", getFileType(getMimeType(uri)))
            mViewModel.setFileState(FileState.Success(mediaItem))
        }
    }

    private var openMediaViewerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data

            if(data != null) {
                val mediaItem = data.getParcelableExtra<MediaItem>(MEDIA_VIEWER_RESULT_MEDIA_KEY)
                finishActivityWithFile(mediaItem)
            }
        }
    }

    private fun finishActivityWithFile(mediaItem: MediaItem?) {
        val data = Intent()
        data.putExtra(CAMERA_RESULT_MEDIA_KEY, mediaItem)

        setResult(RESULT_OK, data)
        finish()
    }

    fun getMimeType(uri: Uri): String? {
        return if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            contentResolver.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.lowercase(Locale.getDefault())
            )
        }
    }

    private fun getFileType(mimeType: String?): FileType {
        var type = FileType.OTHER
        if(mimeType != null){
            if(mimeType.contains("image")){
                type =  FileType.PICTURE
            } else if(mimeType.contains("video")) {
                type =  FileType.VIDEO
            }
        }
        return type
    }

    private fun finishActivityWithoutFile(){
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
            finishActivityWithoutFile()
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