package com.esafirm.sample.UploadPhoto
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.*
import com.esafirm.sample.CustomImagePickerComponents
import com.esafirm.sample.R
import com.esafirm.sample.databinding.UploadFromDeviceBinding


class UploadFromDeviceFragment : Fragment() {
    private lateinit var binding: UploadFromDeviceBinding
    private val images = arrayListOf<com.esafirm.imagepicker.model.Image>()
    private val imagePickerLauncher = registerImagePicker {
        val firstImage = it.firstOrNull() ?: return@registerImagePicker
        binding.uploadLl.visibility = View.GONE
        Glide.with(binding.imgFragment)
            .load(firstImage.uri)
            .into(binding.imgFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UploadFromDeviceBinding.inflate(inflater, container, false)
        start()
        return binding.root
    }

    private fun createConfig(): ImagePickerConfig {

        context?.let { CustomImagePickerComponents(it, false) }?.let {
            ImagePickerComponentsHolder.setInternalComponent(
                it
            )
        }
        return ImagePickerConfig {
            val isExclude = true
            mode = ImagePickerMode.SINGLE
            language = "in" // Set image picker language
          //  theme = R.style.Theme_App_AdBuilder
            returnMode = ReturnMode.ALL
            isFolderMode = true // set folder mode (false by default)
            isIncludeVideo = false // include video (false by default)
            isOnlyVideo = false // include video (false by default)
            arrowColor = Color.BLACK // set toolbar arrow up color
            folderTitle = "Gallery" // folder selection title
            imageTitle = "1 selected" // image selection title
            doneButtonText = "OK" // done button text
            showDoneButtonAlways = true // Show done button always or not
            limit = 10 // max images can be selected (99 by default)
            isShowCamera = true // show camera or not (true by default)
            savePath =
                ImagePickerSavePath("Camera") // captured image directory name ("Camera" folder by default)
            savePath = ImagePickerSavePath(
                Environment.getExternalStorageDirectory().path,
                isRelative = false
            ) // can be a full path
            selectedImages = images
            if (isExclude) {
                excludedImages = images.toFiles() // don't show anything on this selected images
            } else {
                selectedImages = images  // original selected images, used in multi mode
            }
        }
    }

    private fun start() {
        imagePickerLauncher.launch(createConfig())
    }


}