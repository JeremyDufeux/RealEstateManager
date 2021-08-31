package com.openclassrooms.realestatemanager.ui.mediaViewer

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.openclassrooms.realestatemanager.databinding.FragmentVideoViewerBinding
import com.openclassrooms.realestatemanager.modules.HiltVideoDownloadServiceModule
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VideoViewerFragment : Fragment() {
    private val  mViewModel : VideoViewerViewModel by viewModels()
    private var mBinding: FragmentVideoViewerBinding? = null

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
            mBinding?.videoViewerFragmentTv?.text = it
        }
        arguments?.getBoolean(BUNDLE_KEY_EDIT_MODE) ?.let {
            if(it) mBinding?.videoViewerFragmentTv?.visibility = View.GONE
        }

        return mBinding!!.root
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

    @Inject
    @HiltVideoDownloadServiceModule.UserAgent
    lateinit var userAgent: String

    @Inject
    lateinit var downloadCache: Cache

    private fun initializePlayer() {
        val dataSourceFactory = DefaultDataSourceFactory(requireContext(), userAgent)
        val cachedDataSourceFactory = CacheDataSourceFactory(downloadCache, dataSourceFactory)
        val mediaSources = ProgressiveMediaSource.Factory(cachedDataSourceFactory).createMediaSource(
            Uri.parse(mViewModel.url))

        mPlayer = SimpleExoPlayer.Builder(requireContext())
            .build()
            .apply {
                mBinding?.videoViewerFragmentEp?.player = this
                mBinding?.videoViewerFragmentEp?.controllerAutoShow = false
                setMediaSource(mediaSources)
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

    override fun onDestroy() {
        super.onDestroy()

        mBinding = null
    }
}