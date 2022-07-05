package com.esafirm.imagepicker.features

import android.content.Intent
import android.widget.ImageView
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto

interface OnPhotoSelectedListener {

    fun onClickPhoto(photo: UnsplashPhoto, imageView: ImageView)

    fun onLongClickPhoto(photo: UnsplashPhoto, imageView: ImageView)

    fun finishPickUnsplashImages(result: Intent?)

}