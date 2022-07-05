package com.esafirm.sample.UploadPhoto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.sample.R
import com.squareup.picasso.Picasso


class testuploadactivity : AppCompatActivity() {
    private lateinit var img: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_upload)
        img = findViewById(R.id.img_fragment)
        img.setOnClickListener{
            val intent = Intent(this, PhotoUploadActivity::class.java)
            startActivityForResult(intent,432)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, dataI: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataI)
        if (resultCode == Activity.RESULT_OK && requestCode == 432) {
            val images = ImagePicker.getImages(dataI) ?: emptyList()
            val firstImage = images.firstOrNull()
            Glide.with(img)
                .load(firstImage?.uri)
                .into(img)
        }
        else if (resultCode == 2 && requestCode == 432){
            val photo= ImagePicker.getUnsplashImages(dataI)
            Picasso.get().load(photo?.urls?.regular).into(img)
        }
        else if (resultCode == 3 && requestCode == 432){
            val photo= ImagePicker.getDropBoxImages(dataI)
            Glide.with(img)
                .load(photo)
                .into(img)
        }

    }
}



