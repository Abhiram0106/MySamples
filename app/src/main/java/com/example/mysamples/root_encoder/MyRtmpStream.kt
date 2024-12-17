package com.example.mysamples.root_encoder

import android.view.SurfaceHolder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.encoder.utils.gl.AspectRatioMode
import com.pedro.library.view.OpenGlView

@Composable
fun MyRtmpStream(modifier: Modifier = Modifier) {
    val myRtmpRetryHandler = rememberMyRtmpStreamHandler(
        url = "rtmp://18.221.254.170:1935/live/f6bdef60-739e-4b26-a890-7677218f6764"
    )

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        myRtmpRetryHandler.stopStream()
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
                            myRtmpRetryHandler.start(view)
                        }

                        override fun surfaceDestroyed(p0: SurfaceHolder) {
                            myRtmpRetryHandler.stopPreview()
                        }
                    })
                }
            }
        )

        if (myRtmpRetryHandler.isMutedVideo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        }

        Row {
            Button(
                onClick = {
                    myRtmpRetryHandler.switchCamera()
                }
            ) {
                Text(
                    text = if (myRtmpRetryHandler.cameraFacing == CameraHelper.Facing.BACK) {
                        "Back"
                    } else {
                        "Front"
                    }
                )
            }

            Button(
                onClick = { myRtmpRetryHandler.toggleMuteAudio() }
            ) {
                Text(
                    text = if (myRtmpRetryHandler.isMutedAudio) {
                        "Unmute"
                    } else {
                        "Mute"
                    }
                )
            }

            Button(
                onClick = { myRtmpRetryHandler.toggleMuteVideo() }
            ) {
                Text(
                    text = if (myRtmpRetryHandler.isMutedVideo) {
                        "enable cam"
                    } else {
                        "disable cam"
                    }
                )
            }

            if (myRtmpRetryHandler.connectionDisconnected) {
                Button(
                    onClick = { myRtmpRetryHandler.manualRetryConnection() }
                ) {
                    Text(
                        text = "manual retry"
                    )
                }
            }
        }
    }
}
