package com.example.mysamples.root_encoder

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.pedro.common.ConnectChecker
import com.pedro.encoder.input.sources.audio.MicrophoneSource
import com.pedro.encoder.input.sources.video.Camera2Source
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.library.rtmp.RtmpStream
import com.pedro.library.view.OpenGlView
import kotlin.math.min

@Composable
fun rememberMyRtmpStreamHandler(url: String, retries: Int = 5): MyRtmpStreamHandler {
    val context = LocalContext.current
    return remember { MyRtmpStreamHandler(context = context, retries = retries, url = url) }
}

class MyRtmpStreamHandler(
    context: Context,
    retries: Int = 5,
    private val url: String,
) : ConnectChecker {

    /**
    Actual retry counter is in RtmpClient.kt
    This is manually setup to mimic that counter
     */
    private var retryDelayMultiplier = 0
    var connectionDisconnected: Boolean by mutableStateOf(false)
        private set

    private val rtmpStream = RtmpStream(context, this).also {
        it.audioSource.echoCanceler = true
        it.audioSource.noiseSuppressor = true
        (it.videoSource as Camera2Source).apply {
            enableAutoExposure()
            enableAutoFocus()
            enableVideoStabilization()
            enableOpticalVideoStabilization()
            it.getStreamClient().setReTries(retries)
        }
    }

    private var prepared: Boolean = false

    fun start(view: OpenGlView) {
        startPreview(view)
        startStream()
    }

    private fun startPreview(view: OpenGlView) {

        if (!prepared) {
            val videoPrepared = rtmpStream.prepareVideo(
                width = view.width,
                height = view.height,
                rotation = 90,
                bitrate = 1200 * 1000
            )
            val audioPrepared = rtmpStream.prepareAudio(
                32000, true, 128 * 1000
            )
            prepared = videoPrepared && audioPrepared
        }

        if (rtmpStream.isOnPreview) {
            stopPreview()
        }

        rtmpStream.startPreview(view)
    }

    private fun startStream() {
        if (!rtmpStream.isStreaming) {
            rtmpStream.startStream(url)
        }
    }

    fun stopPreview() {
        rtmpStream.videoSource.stop()
        rtmpStream.stopPreview()
    }

    fun stopStream() {
//        rtmpStream.release()
        rtmpStream.stopStream()
        prepared = false
    }

    fun release() {
        rtmpStream.release()
    }

    private val audioSource = rtmpStream.audioSource as MicrophoneSource
    private val videoSource = rtmpStream.videoSource as Camera2Source

    var isMutedAudio: Boolean by mutableStateOf(audioSource.isMuted())
        private set

    fun toggleMuteAudio() {
        if (isMutedAudio) {
            audioSource.unMute()
        } else {
            audioSource.mute()
        }
        isMutedAudio = audioSource.isMuted()
    }

    var cameraFacing: CameraHelper.Facing by mutableStateOf(videoSource.getCameraFacing())
        private set

    fun switchCamera() {
        videoSource.switchCamera()
        cameraFacing = videoSource.getCameraFacing()
    }

    var isMutedVideo: Boolean by mutableStateOf(rtmpStream.getGlInterface().isVideoMuted)
        private set

    fun toggleMuteVideo() {
        if (isMutedVideo) {
            rtmpStream.getGlInterface().unMuteVideo()
        } else {
            rtmpStream.getGlInterface().muteVideo()
        }
        isMutedVideo = rtmpStream.getGlInterface().isVideoMuted
    }

    fun manualRetryConnection() {
        startStream()
    }

    override fun onAuthError() {
        Log.e("MyRtmpStream", "onAuthError")
    }

    override fun onAuthSuccess() {
        Log.e("MyRtmpStream", "onAuthSuccess")
    }

    override fun onConnectionFailed(reason: String) {
        connectionDisconnected = false
        Log.e("MyRtmpStream", "onConnectionFailed: $reason")
        val delay: Long = min(retryDelayMultiplier * 1000L, 5000L)
        val isRetrying = rtmpStream.getStreamClient().reTry(delay, reason)
        retryDelayMultiplier++
        if (isRetrying) {
            Log.e("MyRtmpStream", "retrying: count = $retryDelayMultiplier")
        } else {
            Log.e("MyRtmpStream", "giving up: count = $retryDelayMultiplier")
            rtmpStream.stopStream()
        }
    }

    override fun onConnectionStarted(url: String) {
        Log.e("MyRtmpStream", "onConnectionStarted: $url")
        connectionDisconnected = false
    }

    override fun onConnectionSuccess() {
        Log.e("MyRtmpStream", "onConnectionSuccess")
        retryDelayMultiplier = 0
        connectionDisconnected = false
    }

    override fun onDisconnect() {
        Log.e("MyRtmpStream", "onDisconnect")
        retryDelayMultiplier = 0
        connectionDisconnected = true
    }

}