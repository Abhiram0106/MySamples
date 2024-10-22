package com.example.mysamples.image_cropper

import android.net.Uri

interface ITempFileCreator {
    fun createFile(): Uri
    fun close(): Boolean
}