package com.example.mysamples.image_cropper

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.Closeable

@Composable
fun rememberMyImageManager(selection: (uri: Uri?) -> Unit): MyImageManager {
    val context = LocalContext.current
    val myImageManager = remember {
        MyImageManager(
            context = context,
            selection = selection
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            myImageManager.close()
        }
    }
    return myImageManager
}

class MyImageManager(context: Context, selection: (uri: Uri?) -> Unit) : Closeable {
    private val registry = (context as ComponentActivity).activityResultRegistry
    private val tempFile by lazy {
        MyTempFileCreator(context).also { it.invoke() }
    }
    private val takePicture: ActivityResultLauncher<Uri> by lazy {
        registry.register("take_photo_key", ActivityResultContracts.TakePicture()) { success ->
            selection(if (success) tempFile.photoUri else null)
        }
    }
    private val selectPhoto: ActivityResultLauncher<PickVisualMediaRequest> =
        registry.register("select_picture_key", ActivityResultContracts.PickVisualMedia()) { uri ->
            selection(uri)
        }

    fun selectImage() {
        selectPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun takePhoto() {
        takePicture.launch(tempFile.photoUri)
    }

    override fun close() {
        takePicture.unregister()
        selectPhoto.unregister()
        tempFile.close()
    }
}