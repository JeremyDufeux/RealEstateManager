package com.openclassrooms.realestatemanager.services

import android.content.Context
import android.hardware.SensorManager
import android.view.OrientationEventListener
import com.openclassrooms.realestatemanager.models.OrientationMode
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class OrientationService @Inject constructor(context: Context)
    : OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL){

    private var _orientationModeFlow = MutableStateFlow(OrientationMode.ORIENTATION_PORTRAIT_NORMAL)
    val orientationModeFlow = _orientationModeFlow

    private var _rotationFlow: MutableStateFlow<Float?> = MutableStateFlow(null)
    val rotationFlow = _rotationFlow

    override fun onOrientationChanged(orientation: Int) {
        val lastOrientation = _orientationModeFlow.value
        var orientationMode = lastOrientation

        when{
            orientation >= 315 || orientation < 45 -> {
                if (orientationMode != OrientationMode.ORIENTATION_PORTRAIT_NORMAL) {
                    orientationMode = OrientationMode.ORIENTATION_PORTRAIT_NORMAL
                }
            }
            orientation in 225..314 ->{
                if (orientationMode != OrientationMode.ORIENTATION_LANDSCAPE_NORMAL) {
                    orientationMode = OrientationMode.ORIENTATION_LANDSCAPE_NORMAL
                }
            }
            orientation in 135..224 ->{
                if (orientationMode != OrientationMode.ORIENTATION_PORTRAIT_INVERTED) {
                    orientationMode = OrientationMode.ORIENTATION_PORTRAIT_INVERTED
                }
            }
            else -> {
                if (orientationMode != OrientationMode.ORIENTATION_LANDSCAPE_INVERTED) {
                    orientationMode = OrientationMode.ORIENTATION_LANDSCAPE_INVERTED
                }
            }
        }

        if (lastOrientation != orientationMode) {
            _orientationModeFlow.value = orientationMode

            if(_rotationFlow.value == null){
                _rotationFlow.value = initRotation(orientationMode)
            }
            else{
                calculateRotation(lastOrientation, orientationMode)
            }
        }
    }

    private fun initRotation(orientation: OrientationMode): Float {
         return when(orientation){
            OrientationMode.ORIENTATION_PORTRAIT_NORMAL -> 0f
            OrientationMode.ORIENTATION_PORTRAIT_INVERTED -> 180f
            OrientationMode.ORIENTATION_LANDSCAPE_NORMAL -> 90f
            OrientationMode.ORIENTATION_LANDSCAPE_INVERTED -> -90f
        }
    }

    private fun calculateRotation(lastOrientation: OrientationMode, orientation: OrientationMode) {
        var rotation: Float = _rotationFlow.value!!
        when (lastOrientation) {
            OrientationMode.ORIENTATION_PORTRAIT_NORMAL -> {
                if (orientation == OrientationMode.ORIENTATION_LANDSCAPE_NORMAL) {
                    rotation += 90
                } else if (orientation == OrientationMode.ORIENTATION_LANDSCAPE_INVERTED) {
                    rotation -= 90
                }
            }
            OrientationMode.ORIENTATION_PORTRAIT_INVERTED -> {
                if (orientation == OrientationMode.ORIENTATION_LANDSCAPE_NORMAL) {
                    rotation -= 90
                } else if (orientation == OrientationMode.ORIENTATION_LANDSCAPE_INVERTED) {
                    rotation += 90
                }
            }
            OrientationMode.ORIENTATION_LANDSCAPE_NORMAL -> {
                if (orientation == OrientationMode.ORIENTATION_PORTRAIT_NORMAL) {
                    rotation -= 90
                } else if (orientation == OrientationMode.ORIENTATION_PORTRAIT_INVERTED) {
                    rotation += 90
                }
            }
            OrientationMode.ORIENTATION_LANDSCAPE_INVERTED -> {
                if (orientation == OrientationMode.ORIENTATION_PORTRAIT_NORMAL) {
                    rotation += 90
                } else if (orientation == OrientationMode.ORIENTATION_PORTRAIT_INVERTED) {
                    rotation -= 90
                }
            }
        }
        _rotationFlow.value = rotation
    }

    fun enableOrientationService(){
        if(canDetectOrientation()) {
            enable()
        }
    }

    fun disableOrientationService(){
        disable()
    }
}