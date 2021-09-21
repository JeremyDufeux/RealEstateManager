package com.openclassrooms.realestatemanager.utils

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.utils.Utils.isInternetAvailable
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ConnectivityTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var context: Context

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun testIsInternetAvailableAssertFalseAndTrue() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            disableConnections()
            delay(CONNECTION_DELAY) // Delay to set for commands may vary depending on devices

            assertThat(isInternetAvailable(context)).isFalse()

            enableConnections()
            delay(CONNECTION_DELAY)
            assertThat(isInternetAvailable(context)).isTrue()
        }
    }
}