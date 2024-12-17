package com.example.mysamples.root_encoder

import android.util.Log
import android.view.SurfaceHolder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.pedro.common.ConnectChecker
import com.pedro.encoder.input.sources.audio.MicrophoneSource
import com.pedro.encoder.input.sources.video.Camera2Source
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.encoder.utils.gl.AspectRatioMode
import com.pedro.library.rtmp.RtmpStream
import com.pedro.library.view.OpenGlView

@Composable
fun MyRtmpStream(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val connectionChecker = remember {
        object : ConnectChecker {
            override fun onAuthError() {
                Log.e("MyRtmpStream", "onAuthError")
            }

            override fun onAuthSuccess() {
                Log.e("MyRtmpStream", "onAuthSuccess")
            }

            override fun onConnectionFailed(reason: String) {
                Log.e("MyRtmpStream", "onConnectionFailed: $reason")
            }

            override fun onConnectionStarted(url: String) {
                Log.e("MyRtmpStream", "onConnectionStarted: $url")
            }

            override fun onConnectionSuccess() {
                Log.e("MyRtmpStream", "onConnectionSuccess")
            }

            override fun onDisconnect() {
                Log.e("MyRtmpStream", "onDisconnect")
            }
        }
    }

    val rtmpStream = remember { RtmpStream(context, connectionChecker) }
        .also {
            it.audioSource.echoCanceler = true
            it.audioSource.noiseSuppressor = true
            (it.videoSource as Camera2Source).enableAutoExposure()
        }


    var prepared by remember {
        mutableStateOf(false)
    }

    var isMuted by remember {
        mutableStateOf((rtmpStream.audioSource as MicrophoneSource).isMuted())
    }

    var cameraFacing by remember {
        mutableStateOf(
            (rtmpStream.videoSource as Camera2Source).getCameraFacing()
        )
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        rtmpStream.release()
        prepared = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                OpenGlView(ctx).apply {
                    val view = this
                    view.setAspectRatioMode(AspectRatioMode.Adjust)
                    view.holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(p0: SurfaceHolder) {
                        }

                        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

                            Log.e("SurfaceChanged", "init")

                            val videoPrepared = rtmpStream.prepareVideo(
                                width = width,
                                height = height,
                                rotation = 90,
                                bitrate = 1200 * 1000
                            )
                            val audioPrepared = rtmpStream.prepareAudio(
                                32000, true, 128 * 1000
                            )

                            prepared = videoPrepared && audioPrepared

                            if (rtmpStream.isOnPreview) {
                                rtmpStream.stopPreview()
                            }

                            rtmpStream.startPreview(view)
                        }

                        override fun surfaceDestroyed(p0: SurfaceHolder) {
                            rtmpStream.videoSource.stop()
                            rtmpStream.stopPreview()
                        }
                    })
                }
            }
        )

        var showBlackScreen by remember {
            mutableStateOf(false)
        }
        if (showBlackScreen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        }

        Row {

            Button(
                onClick = {
                    val cameraSource = (rtmpStream.videoSource as Camera2Source)
                    cameraSource.switchCamera()
                    cameraFacing = cameraSource.getCameraFacing()
                }
            ) {
                Text(
                    text = if (cameraFacing == CameraHelper.Facing.BACK) "Back" else "Front"
                )
            }

            Button(
                onClick = {
                    val audioSource = rtmpStream.audioSource as MicrophoneSource
                    if (isMuted) {
                        audioSource.unMute()
                    } else {
                        audioSource.mute()
                    }
                    isMuted = audioSource.isMuted()
                }
            ) {
                Text(
                    text = if (isMuted) "Unmute" else "Mute"
                )
            }

            Button(
                onClick = {
                    val glInterface = rtmpStream.getGlInterface()
                    if (glInterface.isVideoMuted) {
                        rtmpStream.getGlInterface().unMuteVideo()
                        showBlackScreen = false
                    } else {
                        rtmpStream.getGlInterface().muteVideo()
                        showBlackScreen = true
                    }
                }
            ) {
                Text(
                    text = if (rtmpStream.getGlInterface().isVideoMuted) "enable cam" else "disable cam"
                )
            }
        }
    }

    LaunchedEffect(prepared) {
        Log.e("MyRtmpStream", "prepared = $prepared")
        if (prepared && !rtmpStream.isStreaming) {
            rtmpStream.startStream("rtmp://18.221.254.170:1935/live/f6bdef60-739e-4b26-a890-7677218f6764")
        }
    }
}
