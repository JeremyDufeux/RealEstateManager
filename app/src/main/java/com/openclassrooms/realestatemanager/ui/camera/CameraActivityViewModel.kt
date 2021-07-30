package com.openclassrooms.realestatemanager.ui.camera

import android.hardware.Camera
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.models.sealedClasses.FileState
import com.openclassrooms.realestatemanager.modules.MainCoroutineScope
import com.openclassrooms.realestatemanager.services.OrientationService
import com.openclassrooms.realestatemanager.services.PictureSaver
import com.openclassrooms.realestatemanager.services.VideoRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraActivityViewModel @Inject constructor(
    @MainCoroutineScope private val mMainScope: CoroutineScope,
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
        viewModelScope.launch() {
            mOrientationService.orientationModeFlow.collect { orientation ->
                mPictureSaver.setOrientationMode(orientation)
                mVideoRecorder.setOrientationMode(orientation)
            }
        }

        mMainScope.launch() {
            mPictureSaver.fileStateFlow.collect { pictureFile ->
                _fileLiveData.value = pictureFile
            }
        }

        mMainScope.launch() {
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
