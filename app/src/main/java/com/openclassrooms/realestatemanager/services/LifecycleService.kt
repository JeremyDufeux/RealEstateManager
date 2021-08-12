package com.openclassrooms.realestatemanager.services

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LifecycleService @Inject constructor(): LifecycleObserver {

    private var isAppForeground = false

    init {
        observeLifecycle()
    }

    fun observeLifecycle(){
        try {
            ProcessLifecycleOwner.get().lifecycle.addObserver(this@LifecycleService)
        } catch (e: Exception){
            Timber.e("Error LifecycleService : ${e.message}")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onAppBackgrounded() {
        isAppForeground = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForegrounded() {
        isAppForeground = true
    }

    fun isAppForeground(): Boolean{
        return isAppForeground
    }

}