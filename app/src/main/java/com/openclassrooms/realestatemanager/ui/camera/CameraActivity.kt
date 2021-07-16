package com.openclassrooms.realestatemanager.ui.camera

import android.content.ContentResolver
import android.content.Intent
import android.content.pm.ActivityInfo
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.MimeTypeMap
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityCameraBinding
import com.openclassrooms.realestatemanager.models.FileState
import com.openclassrooms.realestatemanager.models.FileType
import com.openclassrooms.realestatemanager.modules.GlideApp
import com.openclassrooms.realestatemanager.ui.camera.CameraActivityViewModel.CameraMode.*
import com.openclassrooms.realestatemanager.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

const val RESULT_URI_KEY = "RESULT_URI_KEY"
const val RESULT_DESCRIPTION_KEY = "RESULT_DESCRIPTION_KEY"
const val RESULT_FILE_TYPE_KEY = "RESULT_FILE_TYPE_KEY"

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {

    private val mViewModel: CameraActivityViewModel by viewModels()

    private lateinit var mBinding: ActivityCameraBinding

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null

    private lateinit var mPlayer: SimpleExoPlayer

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
                when(mFileState.type){
                    FileType.PICTURE -> showPicture()
                    FileType.VIDEO -> showVideo()
                    else -> {return@Observer}
                }
                showCheckButton()
                releaseCamera()
            }
            is FileState.Error -> {
                showToast(this@CameraActivity, R.string.error_saving_file)
                finishActivityWithoutFile()
            }
            is FileState.Empty -> {}
        }
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
            activityCameraCheckBtn.setOnClickListener {
                finishActivityWithFile()
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
            activityCameraDescriptionEt.setOnEditorActionListener { _, actionId, _ ->
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    finishActivityWithFile()
                }
                return@setOnEditorActionListener true
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

    private fun showPicture() {
        GlideApp.with(this)
            .load(mFileState.uri)
            .into(mBinding.activityCameraPictureIv)

        mBinding.activityCameraPictureIv.visibility = View.VISIBLE
    }

    private fun showVideo() {
        mPlayer = SimpleExoPlayer.Builder(this).build()
        mBinding.apply {
            activityCameraFl.visibility = View.GONE
            activityCameraExoplayer.visibility = View.VISIBLE
            activityCameraExoplayer.player = mPlayer
            activityCameraCheckBtn.visibility = View.VISIBLE
        }
        val mediaItem: MediaItem = MediaItem.fromUri(mFileState.uri)
        mPlayer.setMediaItem(mediaItem)
        mPlayer.prepare()
        mPlayer.play()
    }

    private fun showCheckButton() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR

        mBinding.apply {
            activityCameraCheckBtn.visibility = View.VISIBLE
            activityCameraDescriptionEt.visibility = View.VISIBLE

            activityCameraFl.visibility = View.GONE
            activityCameraCapturePhotoBtn.visibility = View.GONE
            activityCameraCaptureVideoBtn.visibility = View.GONE
            activityCameraGalleryBtn.visibility = View.GONE
            activityCameraVideoBtn.visibility = View.GONE
            activityCameraPhotoBtn.visibility = View.GONE
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
        resultLauncher.launch(arrayOf("image/*", "video/*"))
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                val perms = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, perms)
            }
            Timber.d("Debug  registerForActivityResult: $uri")
            mViewModel.setFileState(FileState.Success(uri, getFileType(getMimeType(uri))))
        }
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

    private fun getFileType(mimeType: String?): FileType{
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

    private fun finishActivityWithFile( ) {
        Timber.d("Debug finishActivityWithFile : ${mFileState.uri.toString()}")

        val data = Intent()
        data.putExtra(RESULT_URI_KEY, mFileState.uri.toString())
        data.putExtra(RESULT_FILE_TYPE_KEY, mFileState.type)
        data.putExtra(
            RESULT_DESCRIPTION_KEY,
            mBinding.activityCameraDescriptionEt.text.toString()
        )
        setResult(RESULT_OK, data)
        finish()
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
        pausePlayer()
        mViewModel.disableOrientationService()
        releaseCamera()
    }

    private fun pausePlayer() {
        if(this::mPlayer.isInitialized){
            mPlayer.pause()
        }
    }

    private fun releaseCamera() {
        mCamera?.stopPreview()
        mCamera?.release()
        mCamera = null
        mPreview?.holder?.removeCallback(mPreview)
    }
}