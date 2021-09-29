package com.openclassrooms.realestatemanager.services

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.enums.FileType
import com.openclassrooms.realestatemanager.models.enums.OrientationMode
import com.openclassrooms.realestatemanager.models.sealedClasses.FileState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class PictureSaver @Inject constructor(
    private val mDefaultScope: CoroutineScope,
    private val mContext: Context)
    : Camera.PictureCallback{

    private val mResolver = mContext.contentResolver

    private var mOrientationMode = OrientationMode.ORIENTATION_PORTRAIT_NORMAL

    private val _fileStateFlow = MutableStateFlow<FileState>(FileState.Empty)
    val fileStateFlow = _fileStateFlow.asStateFlow()

    override fun onPictureTaken(data: ByteArray, camera: Camera?) {
        try {
            mDefaultScope.launch {
                val pictureFile = writeFile(data)

                if(pictureFile != null) {
                    val mediaItem = MediaItem(
                        UUID.randomUUID().toString(),
                        "",
                        pictureFile,
                        "",
                        FileType.PICTURE
                    )
                    _fileStateFlow.value = FileState.Success(mediaItem)
                } else {
                    Timber.e("Error while saving file")
                    _fileStateFlow.value = FileState.Error(R.string.error_saving_file)
                }
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
    private fun writeFile(data: ByteArray) : String? {
        val uri = getPictureUri()

        var pictureData = data
        if(mContext.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            var thePicture = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.size)
            val matrix = Matrix()
            matrix.postRotate(mOrientationMode.rotation.toFloat())
            thePicture =
                Bitmap.createBitmap(thePicture, 0, 0, thePicture.width, thePicture.height, matrix, true)

            val bos = ByteArrayOutputStream()
            thePicture.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            pictureData = bos.toByteArray()
        }

        if (uri != null) {
            mResolver.openOutputStream(uri)?.use {
                it.write(pictureData)
                it.close()
            }
        }

        return uri.toString()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getPictureUri(): Uri?{
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "IMG_$timeStamp.jpg"

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.TITLE, fileName)
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000 )
            put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis() / 1000 )
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT >= 29) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
            }
        }

        return mResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }
}

