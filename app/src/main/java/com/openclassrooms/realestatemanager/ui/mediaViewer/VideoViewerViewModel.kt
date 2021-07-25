package com.openclassrooms.realestatemanager.ui.mediaViewer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoViewerViewModel @Inject constructor() : ViewModel() {
    var mPlayWhenReady = true
    var mIsPlaying = true
    var mCurrentWindow = 0
    var mPlaybackPosition = 0L
    var mUrl : String = ""
}