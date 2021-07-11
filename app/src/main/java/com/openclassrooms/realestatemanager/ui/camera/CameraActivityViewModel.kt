package com.openclassrooms.realestatemanager.ui.camera

import android.hardware.Camera
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.models.FileState
import com.openclassrooms.realestatemanager.services.OrientationService
import com.openclassrooms.realestatemanager.services.PictureSaver
import com.openclassrooms.realestatemanager.services.VideoRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraActivityViewModel @Inject constructor(
    private val mOrientationService: OrientationService,
    private val mPictureSaver: PictureSaver,
    private val mVideoRecorder: VideoRecorder
): ViewModel(){

    val rotationLiveData = mOrientationService.rotationFlow.asLiveData()

    private val _fileLiveData = MutableLiveData<FileState>()
    var fileLiveData : LiveData<FileState> = _fileLiveData

    var cameraMode: CameraMode = CameraMode.PHOTO
    var recording = false

    enum class CameraMode{
        PHOTO, VIDEO
    }

    init{
        viewModelScope.launch(Dispatchers.Default) { // TODO to inject
            mOrientationService.orientationModeFlow.collect { orientation ->
                mPictureSaver.setOrientationMode(orientation)
                mVideoRecorder.setOrientationMode(orientation)
            }
        }

        viewModelScope.launch(Dispatchers.Main) {
            mPictureSaver.fileStateFlow.collect { pictureFile ->
                _fileLiveData.value = pictureFile
            }
        }
        viewModelScope.launch(Dispatchers.Main) {
            mVideoRecorder.fileStateFlow.collect { videoFile ->
                _fileLiveData.value = videoFile
            }
        }
    }

    fun enableOrientationService() {
        mOrientationService.enableOrientationService()
    }

    fun disableOrientationService() {
        mOrientationService.disableOrientationService()
    }

    fun getPictureSaver() : PictureSaver{
        return mPictureSaver
    }

    fun startRecording(camera: Camera) {
        mVideoRecorder.startRecording(camera)
    }

    fun stopRecording() {
        mVideoRecorder.stopRecording()
    }

    fun setFileState(file: FileState.Success) {
        _fileLiveData.value = file
    }
}
