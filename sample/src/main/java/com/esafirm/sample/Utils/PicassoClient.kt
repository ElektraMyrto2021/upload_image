package com.esafirm.sample.Utils

import android.content.Context
import com.esafirm.sample.Utils.FileThumbnailRequestHandler
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso

class PicassoClient {
    private var sPicasso: Picasso? = null

    fun init(context: Context?) {

        // Configure picasso to know about special thumbnail requests
        sPicasso = Picasso.Builder(context!!)
            .downloader(OkHttp3Downloader(context))
            .addRequestHandler(FileThumbnailRequestHandler())
            .build()
    }


    fun getPicasso(): Picasso? {
        return sPicasso
    }
}
