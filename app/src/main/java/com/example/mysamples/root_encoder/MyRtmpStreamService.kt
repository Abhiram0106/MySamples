package com.example.mysamples.root_encoder

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.pedro.common.ConnectChecker

class MyRtmpStreamService: Service(), ConnectChecker {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

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