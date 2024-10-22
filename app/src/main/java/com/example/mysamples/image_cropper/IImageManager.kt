package com.example.mysamples.image_cropper

interface IImageManager: ITempFileCreator {
    fun selectImage()
    fun takePhoto()
}