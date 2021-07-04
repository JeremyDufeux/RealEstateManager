package com.openclassrooms.realestatemanager.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import timber.log.Timber
import java.io.IOException

@SuppressLint("ViewConstructor")
class CameraPreview(context: Context, private val mCamera: Camera )
    : SurfaceView(context), SurfaceHolder.Callback {

    private val mHolder: SurfaceHolder = holder.apply {
        addCallback(this@CameraPreview)
        setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val params = mCamera.parameters

        if(resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE){
            params.set("orientation", "portrait")
            mCamera.setDisplayOrientation(90)
            params.setRotation(90)
        }
        else{
            params.set("orientation", "landscape")
            mCamera.setDisplayOrientation(0)
            params.setRotation(0)
        }

        mCamera.apply {
            try {
                parameters = params
                setPreviewDisplay(holder)
                startPreview()
            }catch (e: IOException) {
                Timber.e("Error surfaceCreated : ${e.message.toString()}")
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if(mHolder.surface == null){
            return
        }

        try {
            mCamera.stopPreview()
        } catch (e: IOException) {
            Timber.e("Error surfaceChanged : ${e.message.toString()}")
        }

        mCamera.apply {
            try {
                setPreviewDisplay(mHolder)
                startPreview()
            }catch (e: IOException) {
                Timber.e("Error surfaceCreated starting camera preview: ${e.message.toString()}")
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }
}