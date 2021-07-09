package com.openclassrooms.realestatemanager.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.services.ImageSaver
import com.openclassrooms.realestatemanager.services.OrientationService
import com.openclassrooms.realestatemanager.services.VideoRecorder
import com.openclassrooms.realestatemanager.ui.camera.CameraActivity.CameraMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraActivityViewModel @Inject constructor(
    private val mOrientationService: OrientationService,
    val mPictureSaver: ImageSaver,
    val mVideoRecorder: VideoRecorder
): ViewModel(){

    val rotationLiveData = mOrientationService.rotationFlow.asLiveData()
    val pictureFileStateFlow = mPictureSaver.fileStateFlow.asLiveData()
    val videoFileStateFlow = mVideoRecorder.fileStateFlow.asLiveData()

    var cameraMode: CameraMode = CameraMode.PHOTO
    var recording = false

    fun startOrientationService() {
        mOrientationService.enableOrientationService()
        viewModelScope.launch(Dispatchers.Default) {
            mOrientationService.orientationModeFlow.collect { orientation ->
                mPictureSaver.setOrientationMode(orientation)
                mVideoRecorder.setOrientationMode(orientation)
            }
        }
    }

    fun enableOrientationService() {
        mOrientationService.enableOrientationService()
    }

    fun disableOrientationService() {
        mOrientationService.disableOrientationService()
    }
}
