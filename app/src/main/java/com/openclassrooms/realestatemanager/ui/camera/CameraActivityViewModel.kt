package com.openclassrooms.realestatemanager.ui.camera

import android.hardware.Camera
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.services.ImageSaver
import com.openclassrooms.realestatemanager.services.OrientationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraActivityViewModel @Inject constructor(
    private val mOrientationService: OrientationService,
    private val mImageSaver: ImageSaver
)
    : ViewModel(){

    fun startOrientationService() {
        mOrientationService.enableOrientationService()
        viewModelScope.launch(Dispatchers.Default) {
            mOrientationService.orientationModeFlow.collect { orientation ->
                mImageSaver.setOrientationMode(orientation)
            }
        }
    }

    val fileStateFlow = mImageSaver.fileStateFlow

    fun disableOrientationService() {
        mOrientationService.disableOrientationService()
    }

    fun takePicture(mCamera: Camera?) {
        viewModelScope.launch(Dispatchers.Default) {
            mCamera?.autoFocus(null)
            mCamera?.takePicture(null, null, mImageSaver)
        }
    }
}
