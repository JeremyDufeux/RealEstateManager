package com.openclassrooms.realestatemanager.services

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.OrientationMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ImageSaver @Inject constructor(private val mContext: Context) : Camera.PictureCallback{

    private var mOrientationMode = OrientationMode.ORIENTATION_PORTRAIT_NORMAL
    private val _fileStateFlow = MutableStateFlow<FileState>(FileState.Empty)
    val fileStateFlow = _fileStateFlow.asStateFlow()

    sealed class FileState{
        data class Success(val file: File) : FileState()
        data class Error(val StringId: Int) : FileState()
        object Empty : FileState()
    }

    override fun onPictureTaken(data: ByteArray, camera: Camera?) {
        val pictureFile: File = getOutputMediaFile(MEDIA_TYPE_IMAGE) ?: kotlin.run {
            Timber.e("Error: creating media file, check storage permissions")
            _fileStateFlow.value = FileState.Error(R.string.error_saving_file)
            return
        }

        val pictureBytes: ByteArray

        if(mContext.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            var thePicture = BitmapFactory.decodeByteArray(data, 0, data.size)
            val m = Matrix()
            m.postRotate(mOrientationMode.rotation.toFloat())
            thePicture =
                Bitmap.createBitmap(thePicture, 0, 0, thePicture.width, thePicture.height, m, true)

            val bos = ByteArrayOutputStream()
            thePicture.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            pictureBytes = bos.toByteArray()
        }
        else{
            pictureBytes = data
        }

        try {
            FileOutputStream(pictureFile).apply {
                write(pictureBytes)
                close()
            }

            val contentResolver = ContentValues().apply {
                put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis())
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.DATA, pictureFile.absolutePath)
            }

            mContext.contentResolver.insert(EXTERNAL_CONTENT_URI, contentResolver)

            _fileStateFlow.value = FileState.Success(pictureFile)

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
    private fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(
            mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            mContext.getString(R.string.app_name)
        )

        mediaStorageDir.apply {
            if(!exists()){
                if(!mkdirs()){
                    Timber.e("Error: failed to create directory")
                    _fileStateFlow.value = FileState.Error(R.string.error_creating_folder)
                }
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return when (type) {
            MEDIA_TYPE_IMAGE -> {
                File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
            }
            MEDIA_TYPE_VIDEO -> {
                File("${mediaStorageDir.path}${File.separator}VID_$timeStamp.mp4")
            }
            else -> null
        }
    }
}