package com.openclassrooms.realestatemanager.services

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.models.enums.OrientationMode
import com.openclassrooms.realestatemanager.models.sealedClasses.FileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class VideoRecorder @Inject constructor(private val mContext: Context) {

    private val mResolver = mContext.contentResolver

    private var mOrientationMode = OrientationMode.ORIENTATION_PORTRAIT_NORMAL

    private val _fileStateFlow = MutableStateFlow<FileState>(FileState.Empty)
    val fileStateFlow = _fileStateFlow.asStateFlow()

    private var mRecorder: MediaRecorder? = null
    private lateinit var mVideoUri: Uri

    fun startRecording(camera: Camera) {
        mRecorder = MediaRecorder().apply {
            setCamera(camera)
            setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            setVideoSource(MediaRecorder.VideoSource.CAMERA)

            setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P))

            setOrientationHint(mOrientationMode.rotation)

            val uri = getVideoUri()

            if (uri != null) {
                mVideoUri = uri
            } else {
                _fileStateFlow.value = FileState.Error(R.string.an_error_append)
                Timber.e("Error: startRecording error when creating file")
                return
            }

            val fileDescriptor = mResolver.openFileDescriptor(uri,"rw" )?.fileDescriptor
            setOutputFile(fileDescriptor)

            try {
                prepare()
                start()
            } catch (e: IOException) {
                Timber.e("Error: startRecording IOException: ${e.message}")
                _fileStateFlow.value = FileState.Error(R.string.an_error_append)
            } catch (e: IllegalStateException) {
                Timber.e("Error: startRecording IllegalStateException: ${e.message}")
                _fileStateFlow.value = FileState.Error(R.string.an_error_append)
            }
        }
    }

    fun stopRecording() {
        mRecorder?.apply {
            stop()
            reset()
            release()
        }
        mRecorder = null

        val mediaItem = MediaItem(UUID.randomUUID().toString(), "", mVideoUri.toString(), "", FileType.VIDEO)

        _fileStateFlow.value = FileState.Success(mediaItem)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getVideoUri(): Uri?{
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "VID_$timeStamp.mp4"

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis())
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if(Build.VERSION.SDK_INT >= 29) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
            }
        }

        return mResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
    }

    fun setOrientationMode(orientation : OrientationMode){
        mOrientationMode = orientation
    }
}