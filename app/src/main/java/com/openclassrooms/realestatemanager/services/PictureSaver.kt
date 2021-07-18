package com.openclassrooms.realestatemanager.services

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import androidx.core.net.toUri
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.FileState
import com.openclassrooms.realestatemanager.models.FileType
import com.openclassrooms.realestatemanager.models.OrientationMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class PictureSaver @Inject constructor(
    private val mDefaultScope: CoroutineScope,
    private val mContext: Context)
    : Camera.PictureCallback{

    private var mOrientationMode = OrientationMode.ORIENTATION_PORTRAIT_NORMAL

    private val _fileStateFlow = MutableStateFlow<FileState>(FileState.Empty)
    val fileStateFlow = _fileStateFlow.asStateFlow()

    private lateinit var mPictureFile: File
    private lateinit var mPictureBytes: ByteArray

    override fun onPictureTaken(data: ByteArray, camera: Camera?) {
        mPictureBytes = data
        mPictureFile = getOutputMediaFile()

        try {
            mDefaultScope.launch {
                writeFile()

                addPictureToGallery()
                _fileStateFlow.value = FileState.Success(mPictureFile.toUri(), FileType.PICTURE)
            }
        }
        catch (e: FileNotFoundException){
            Timber.e("Error: File not found: ${e.message}")
            _fileStateFlow.value = FileState.Error(R.string.error_saving_file)
        }
        catch (e: IOException){
            Timber.e("Error: \"Error accessing file: ${e.message}")
            _fileStateFlow.value = FileState.Error(R.string.error_saving_file)
        }
    }

    fun setOrientationMode(orientation : OrientationMode){
        mOrientationMode = orientation
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
        return File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
    }

    private fun writeFile(){
        if(mContext.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            var thePicture = BitmapFactory.decodeByteArray(mPictureBytes, 0, mPictureBytes.size)
            val m = Matrix()
            m.postRotate(mOrientationMode.rotation.toFloat())
            thePicture =
                Bitmap.createBitmap(thePicture, 0, 0, thePicture.width, thePicture.height, m, true)

            val bos = ByteArrayOutputStream()
            thePicture.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            mPictureBytes = bos.toByteArray()
        }

        FileOutputStream(mPictureFile).apply {
            write(mPictureBytes)
            close()
        }
    }

    private fun addPictureToGallery() {
        val contentResolver = ContentValues().apply {
            put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.DATA, mPictureFile.absolutePath)
        }
        mContext.contentResolver.insert(EXTERNAL_CONTENT_URI, contentResolver)

        mContext.sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())
            )
        )
    }
}