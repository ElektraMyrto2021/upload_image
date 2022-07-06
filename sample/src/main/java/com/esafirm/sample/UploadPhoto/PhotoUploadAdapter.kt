package com.esafirm.sample.UploadPhoto

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.esafirm.imagepicker.features.ImagePickerFragment
import com.github.basshelal.unsplashpicker.presentation.PhotoPickerFragment


class PhotoUploadAdapter(fragment: PhotoUploadActivity) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ImagePickerFragment.newInstance()
             1 -> PhotoPickerFragment()
            2 -> UploadFromDropBoxFragment()
            else -> {
                ImagePickerFragment.newInstance()
            }
        }
    }
}