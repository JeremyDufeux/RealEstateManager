package com.openclassrooms.realestatemanager

import androidx.hilt.work.HiltWorkerFactory
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import com.openclassrooms.realestatemanager.services.LifecycleService
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp(MultiDexApplication::class)
class RealEstateManagerApplication:
    Hilt_RealEstateManagerApplication(),
    Configuration.Provider {

    @Inject
    lateinit var mWorkerFactory: HiltWorkerFactory

    @Inject
    lateinit var mLifecycleService: LifecycleService

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        mLifecycleService.observeLifecycle()
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(mWorkerFactory)
            .build()
}