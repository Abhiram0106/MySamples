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

@Composable
fun rememberMyImageManager(selection: (uri: Uri?) -> Unit): MyImageManager {
    val context = LocalContext.current
    val myImageManager = remember {
        MyImageManager(
            context = context,
            tempFileCreator = MyTempFileCreator(context),
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

class MyImageManager(
    context: Context,
    private val tempFileCreator: ITempFileCreator,
    selection: (uri: Uri?) -> Unit
) : IImageManager, ITempFileCreator by tempFileCreator {
    private val registry = (context as ComponentActivity).activityResultRegistry
    private val tempFileUri by lazy {
        tempFileCreator.createFile()
    }
    private val takePicture: ActivityResultLauncher<Uri> by lazy {
        registry.register("take_photo_key", ActivityResultContracts.TakePicture()) { success ->
            selection(if (success) tempFileUri else null)
        }
    }
    private val selectPhoto: ActivityResultLauncher<PickVisualMediaRequest> by lazy {
        registry.register("select_picture_key", ActivityResultContracts.PickVisualMedia()) { uri ->
            selection(uri)
        }
    }

    override fun selectImage() {
        selectPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun takePhoto() {
        takePicture.launch(tempFileUri)
    }

    override fun close(): Boolean {
        takePicture.unregister()
        selectPhoto.unregister()
        return tempFileCreator.close()
    }
}