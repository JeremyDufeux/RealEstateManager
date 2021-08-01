package com.openclassrooms.realestatemanager.ui.mediaViewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.openclassrooms.realestatemanager.databinding.FragmentVideoViewerBinding


class VideoViewerFragment : Fragment() {
    private val  mViewModel : VideoViewerViewModel by viewModels()
    private lateinit var mBinding: FragmentVideoViewerBinding

    private var mPlayer: SimpleExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentVideoViewerBinding.inflate(inflater)

        arguments?.getString(BUNDLE_KEY_MEDIA_URL) ?.let {
            mViewModel.url = it
        }
        arguments?.getString(BUNDLE_KEY_MEDIA_DESCRIPTION) ?.let {
            if (it.isNotBlank() && it.isNotEmpty()) {
                mBinding.videoViewerFragmentTv.text = it
                mBinding.videoViewerFragmentTv.visibility = View.VISIBLE
            }
        }

        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        if(mViewModel.isPlaying) {
            mPlayer?.play()
        }
    }

    override fun onPause() {
        super.onPause()
        if(mPlayer?.isPlaying == true) {
            mViewModel.isPlaying = true
            mPlayer?.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        val mediaItem: MediaItem = MediaItem.fromUri(mViewModel.url)
        mPlayer = SimpleExoPlayer.Builder(requireContext())
            .build()
            .apply {
                mBinding.videoViewerFragmentEp.player = this
                mBinding.videoViewerFragmentEp.controllerAutoShow = false
                setMediaItem(mediaItem)
                repeatMode = Player.REPEAT_MODE_ALL
                playWhenReady = mViewModel.playWhenReady
                seekTo(mViewModel.currentWindow, mViewModel.playbackPosition)
                prepare()
            }
    }

    private fun releasePlayer() {
        mPlayer?.run {
            mViewModel.playbackPosition = currentPosition
            mViewModel.currentWindow = currentWindowIndex
            mViewModel.playWhenReady = playWhenReady
            release()
        }
        mPlayer = null
    }
}