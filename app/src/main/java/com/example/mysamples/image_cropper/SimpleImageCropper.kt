package com.example.mysamples.image_cropper

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleImageCropper(modifier: Modifier = Modifier) {

    var pic by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val test = rememberMyImageManager { uri ->
        println("uri = $uri")
        uri?.let {
            scope.launch {
                pic = it.toImageBitmapUsingBitmapFactory(context)
            }
        }
    }
    val sheetState = rememberModalBottomSheetState()
//    val pickMedia =
//        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
//            println("uri = $uri")
//            uri?.let {
//                scope.launch {
//                    pic = it.toImageBitmapUsingBitmapFactory(context)
//                }
//            }
//        }
//    val tempFileCreator = rememberMyTempFileCreator()
//    val takePhoto =
//        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
//            println("Take photo success = $it")
//            if (it) {
//                scope.launch {
//                    pic = tempFileCreator.photoUri.toImageBitmapUsingBitmapFactory(context)
//                }
//            }
//        }

    LaunchedEffect(pic) {
        println("pic changed, is null = ${pic == null}")
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (pic != null) {
            Image(
                bitmap = pic!!,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5F)
            )
        }

        Button(onClick = { showBottomSheet = true }) {
            Text(
                text = "select picture",
                style = MaterialTheme.typography.labelLarge
            )
        }

    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column {
                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
//                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        test.selectImage()
                    }
                ) {
                    Text(
                        text = "Gallery",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet = false
                        }
//                        if (tempFileCreator.photoUri != Uri.EMPTY) {
//                            takePhoto.launch(tempFileCreator.photoUri)
//                        }
                        test.takePhoto()
                    }
                ) {
                    Text(
                        text = "Camera",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}