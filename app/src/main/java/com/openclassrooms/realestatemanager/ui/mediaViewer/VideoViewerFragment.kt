package com.openclassrooms.realestatemanager.ui.mediaViewer

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
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
            mViewModel.mUrl = it
        }
        arguments?.getString(BUNDLE_KEY_MEDIA_DESCRIPTION) ?.let {
            mBinding.videoViewerFragmentTv.text = it
        }

        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        if(mViewModel.mIsPlaying) {
            mPlayer?.play()
        }
    }

    override fun onPause() {
        super.onPause()
        if(mPlayer?.isPlaying == true) {
            mViewModel.mIsPlaying = true
            mPlayer?.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(mViewModel.mUrl)))

        mPlayer = SimpleExoPlayer.Builder(requireContext())
            .build()
            .apply {
                mBinding.videoViewerFragmentEp.player = this
                mBinding.videoViewerFragmentEp.controllerAutoShow = false
                setMediaSource(source)
                repeatMode = Player.REPEAT_MODE_ALL;
                playWhenReady = mViewModel.mPlayWhenReady
                seekTo(mViewModel.mCurrentWindow, mViewModel.mPlaybackPosition)
                prepare()
            }
    }

    private fun releasePlayer() {
        mPlayer?.run {
            mViewModel.mPlaybackPosition = currentPosition
            mViewModel.mCurrentWindow = currentWindowIndex
            mViewModel.mPlayWhenReady = playWhenReady
            release()
        }
        mPlayer = null
    }
}