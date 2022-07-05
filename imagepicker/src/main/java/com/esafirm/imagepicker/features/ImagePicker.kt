package com.esafirm.imagepicker.features

import android.content.Intent
import android.net.Uri
import com.esafirm.imagepicker.features.cameraonly.ImagePickerCameraOnly
import com.esafirm.imagepicker.model.Image
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto

import java.io.File

object ImagePicker {
    fun cameraOnly(): ImagePickerCameraOnly {
        return ImagePickerCameraOnly()
    }

    /* --------------------------------------------------- */
    /* > Helper */
    /* --------------------------------------------------- */

    @Deprecated("This method will marked internal soon. Please use the new API",
        ReplaceWith("intent?.getParcelableArrayListExtra(IpCons.EXTRA_SELECTED_IMAGES)")
    )
    fun getImages(intent: Intent?): List<Image>? {
        return intent?.getParcelableArrayListExtra(IpCons.EXTRA_SELECTED_IMAGES)
    }

    fun getUnsplashImages(intent: Intent?): UnsplashPhoto? {
        return intent?.getParcelableExtra("EXTRA_PHOTOS")
    }
    fun getDropBoxImages(intent: Intent?): Uri? {
        return intent?.getParcelableExtra("DropBox_PHOTOS")
    }

}