package com.example.mysamples.image_cropper

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Uri.toImageBitmapUsingBitmapFactory(
    context: Context,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): ImageBitmap? {
    var bitmap: ImageBitmap? = null
    return withContext(dispatcher) {
        context.contentResolver.openInputStream(this@toImageBitmapUsingBitmapFactory)
            .use { inputStream ->
                bitmap = BitmapFactory.decodeStream(inputStream).asImageBitmap()
            }
        bitmap
    }
}

suspend fun Uri.toImageBitmapUsingImageDecoder(
    context: Context,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): ImageBitmap? {
    val source = ImageDecoder.createSource(context.contentResolver, this)
    return withContext(dispatcher) {
        try {
            ImageDecoder.decodeBitmap(source).asImageBitmap()
        } catch (e: ImageDecoder.DecodeException) {
            e.printStackTrace()
            null
        }
    }
}