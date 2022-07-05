package com.esafirm.sample.Utils

import android.net.Uri
import com.dropbox.core.DbxException
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.ThumbnailFormat
import com.dropbox.core.v2.files.ThumbnailSize
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import okio.source
import java.io.IOException

 class FileThumbnailRequestHandler : RequestHandler() {

    val SCHEME = "dropbox"
    val HOST = "dropbox"
    var mDbxClient: DbxClientV2? = null

//    fun FileThumbnailRequestHandler() {
//        mDbxClient = dbxClient
//    }

    /**
     * Builds a [Uri] for a Dropbox file thumbnail suitable for handling by this handler
     */
    fun buildPicassoUri(file: FileMetadata): Uri? {
        return Uri.Builder()
            .scheme(SCHEME)
            .authority(HOST)
            .path(file.pathDisplay).build()
    }

    override fun canHandleRequest(data: Request): Boolean {
        return SCHEME == data.uri.scheme && HOST == data.uri.host
    }

    @Throws(IOException::class)
    override fun load(request: Request, networkPolicy: Int): Result? {
        return try {
            val downloader = mDbxClient!!.files().getThumbnailBuilder(request.uri.path)
                //.withFormat(ThumbnailFormat.JPEG)
                .withSize(ThumbnailSize.W1024H768)
                .start()
            Result(downloader.inputStream.source(), Picasso.LoadedFrom.NETWORK)
        } catch (e: DbxException) {
            throw IOException(e)
        }
    }
}
