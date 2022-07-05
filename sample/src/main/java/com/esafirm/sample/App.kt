package com.esafirm.sample
import android.app.Application
import com.github.basshelal.unsplashpicker.UnsplashPhotoPickerConfig

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        UnsplashPhotoPickerConfig.init(
            application = this,
            accessKey = "z5kbG6I9gxXdaQ4udAx9Ir7i0ICYubcww3TtW3zBA4c",
            secretKey = "bsPK3IQCgdfvA32GnPUxM05Z1_jQjIgo7Ki9rVz0j9I",
            unsplashAppName = "adbuilder",
            isLoggingEnabled = false
        )
    }
}