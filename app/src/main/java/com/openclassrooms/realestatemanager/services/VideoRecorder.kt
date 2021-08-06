package com.openclassrooms.realestatemanager.services

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.models.enums.OrientationMode
import com.openclassrooms.realestatemanager.models.sealedClasses.FileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class VideoRecorder @Inject constructor(private val mContext: Context) {

    private var mOrientationMode = OrientationMode.ORIENTATION_PORTRAIT_NORMAL

    private val _fileStateFlow = MutableStateFlow<FileState>(FileState.Empty)
    val fileStateFlow = _fileStateFlow.asStateFlow()

    private var mRecorder: MediaRecorder? = null
    private lateinit var mVideoFile: File

    fun startRecording(camera: Camera) {
        mRecorder = MediaRecorder().apply {
            setCamera(camera)
            setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            setVideoSource(MediaRecorder.VideoSource.CAMERA)

            setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P))

            setOrientationHint(mOrientationMode.rotation)

            mVideoFile = getOutputMediaFile()
            if(Build.VERSION.SDK_INT < 26) {
                setOutputFile(mVideoFile.absolutePath)
            }else{
                setOutputFile(mVideoFile)
            }

            try {
                prepare()
                start()
            } catch (e: IOException) {
                Timber.e("Debug startRecording IOException: ${e.message}")
                _fileStateFlow.value = FileState.Error(R.string.an_error_append)
            } catch (e: IllegalStateException) {
                Timber.e("Debug startRecording IllegalStateException: ${e.message}")
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
        addVideoToGallery()

        val mediaItem = MediaItem(UUID.randomUUID().toString(), "", mVideoFile.toUri().toString(), "", FileType.VIDEO)

        _fileStateFlow.value = FileState.Success(mediaItem)
    }

    @SuppressLint("SimpleDateFormat")
    fun getOutputMediaFile(): File {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            mContext.getString(R.string.app_name)
        )

        mediaStorageDir.apply {
            if(!exists()){
                if(!mkdirs()){
                    _fileStateFlow.value = FileState.Error(R.string.error_creating_folder)
                }
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        return File("${mediaStorageDir.path}${File.separator}VID_$timeStamp.mp4")
    }

    fun setOrientationMode(orientation : OrientationMode){
        mOrientationMode = orientation
    }

    private fun addVideoToGallery() {
        val contentResolver = ContentValues().apply {
            put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis())
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.DATA, mVideoFile.absolutePath)
        }
        mContext.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentResolver)

        mContext.sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())
            )
        )
    }
}