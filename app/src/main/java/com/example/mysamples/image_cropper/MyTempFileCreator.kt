package com.example.mysamples.image_cropper

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.time.LocalDateTime

@Composable
fun rememberMyTempFileCreator(): MyTempFileCreator {
    val context = LocalContext.current
    val myTempFileCreator = remember {
        MyTempFileCreator(context)
    }
    DisposableEffect(Unit) {
        myTempFileCreator.createFile()
        onDispose {
            myTempFileCreator.close()
        }
    }
    return myTempFileCreator
}

class MyTempFileCreator(private val context: Context) : ITempFileCreator {

    private var photoUri: Uri = Uri.EMPTY
    private val timeStamp: String = LocalDateTime.now().toString()
    private val storageDir: File? = context.externalCacheDir

    private var photoFile: File = File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )

    override fun createFile(): Uri {
        photoUri = FileProvider.getUriForFile(
            context,
            "com.example.mysamples.fileprovider",
            photoFile
        )
        return photoUri
    }

    override fun close(): Boolean {
        return photoFile.delete().also { success ->
            println("CLOSING MyTempFileCreator $success")
        }
    }
}