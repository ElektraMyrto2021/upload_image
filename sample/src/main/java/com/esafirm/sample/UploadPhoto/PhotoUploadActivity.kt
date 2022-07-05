package com.esafirm.sample.UploadPhoto

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.dropbox.core.android.AuthActivity.result
import com.esafirm.imagepicker.features.ImagePickerFragment
import com.esafirm.imagepicker.features.ImagePickerInteractionListener
import com.esafirm.imagepicker.features.OnPhotoSelectedListener
import com.esafirm.imagepicker.model.Image
import com.esafirm.sample.R
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class PhotoUploadActivity : AppCompatActivity(), ImagePickerInteractionListener, OnPhotoSelectedListener {
       //

    var tabLayout: TabLayout? = null
    private lateinit var viewPager: ViewPager2
    private lateinit var imagePickerFragment: ImagePickerFragment
    private val images = arrayListOf<com.esafirm.imagepicker.model.Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      setContentView(R.layout.upload_photo_adbuilder)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        tabLayout!!.tabGravity = TabLayout.GRAVITY_CENTER
        val adapter = PhotoUploadAdapter(this)
        viewPager.adapter = adapter
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
        })

        TabLayoutMediator(tabLayout!!, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Upload"
                }
                1 -> {
                    tab.text = "Unsplash"
                }
                2 -> {
                    tab.text = "Dropbox"
                }
            }
        }.attach()


    }


    override fun setTitle(title: String?) {
        actionBar?.title = title
        invalidateOptionsMenu()
    }

    override fun cancel() {
        finish()
    }

    override fun selectionChanged(imageList: List<Image>?) {
        // Do nothing when the selection changes.
    }

    override fun finishPickImages(result: Intent?) {
        setResult(RESULT_OK, result)
        finish()

    }
//
//       override fun onClickPhoto(photo: UnsplashPhoto, imageView: ImageView) {
//           setResult(RESULT_OK, result)
//           finish()
//       }
//
//       override fun onLongClickPhoto(photo: UnsplashPhoto, imageView: ImageView) {
//           setResult(RESULT_OK, result)
//           finish()
//       }

    override fun onClickPhoto(
        photo: UnsplashPhoto,
        imageView: ImageView
    ) {
        setResult(RESULT_OK, result)
        finish()
        TODO("Not yet implemented")
    }

    override fun onLongClickPhoto(
        photo: UnsplashPhoto,
        imageView: ImageView
    ) {
        setResult(RESULT_OK, result)
        finish()
        TODO("Not yet implemented")
    }

    override fun finishPickUnsplashImages(result: Intent?) {
           setResult(RESULT_OK, result)
           finish()
       }


   }