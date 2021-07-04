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
        }
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