package com.openclassrooms.realestatemanager.utils

import androidx.test.platform.app.InstrumentationRegistry

const val CONNECTION_DELAY = 2000L

fun disableConnections(){
    InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("svc wifi disable")
    InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("svc data disable")
}

fun enableConnections(){
    InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("svc wifi enable")
    InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("svc data enable")
}