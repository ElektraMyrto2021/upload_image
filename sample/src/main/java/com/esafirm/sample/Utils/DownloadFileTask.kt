package com.esafirm.sample.Utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import com.dropbox.core.DbxException
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class DownloadFileTask(context: Context?, dbxClient: DbxClientV2?, param: Callback) :
    AsyncTask<FileMetadata, Void, File>() {

    var mContext: Context? = context
    var mDbxClient: DbxClientV2? = dbxClient
    var mCallback: Callback? = param
    var mException: Exception? = null

    interface Callback {
        fun onDownloadComplete(result: File?)
        fun onFinishProcess(result: Intent)
        fun onError(e: Exception?)
    }

//    fun DownloadFileTask(context: Context?, dbxClient: DbxClientV2?, callback: Callback?) {
//        mContext = context
//        mDbxClient = dbxClient
//        mCallback = callback
//    }

    override fun onPostExecute(result: File?) {
        super.onPostExecute(result)
        if (mException != null) {
            mCallback?.onError(mException)
        } else {
            mCallback?.onDownloadComplete(result)
        }
    }

    override fun doInBackground(vararg params: FileMetadata): File? {
        val metadata = params[0]
        try {

//
//            val path1 =
//                File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES)
//            val uri = Uri.fromFile(path1)
//            val intent2: Intent =
//                getPackageManager().getLaunchIntentForPackage("com.sec.android.app.myfiles")
//            intent.action = "samsung.myfiles.intent.action.LAUNCH_MY_FILES"
//            intent.putExtra("samsung.myfiles.intent.extra.START_PATH", path1.absolutePath)
//            startActivity(intent)
//            val RootDir = Environment.getExternalStorageDirectory()
//                .toString() + File.separator + "Steps"
//
//            val RootFile = File(RootDir)
//            RootFile.mkdir()

            val path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            val file = File(path, metadata.name)

            // Make sure the directory exists.
            if (!path.exists()) {
                if (!path.mkdirs()) {
                    mException = RuntimeException("Unable to create directory: $path")
                }
            } else if (!path.isDirectory) {
                mException = IllegalStateException("Download path is not a directory: $path")
                return null
            }
            FileOutputStream(file).use { outputStream ->
                mDbxClient!!.files().download(metadata.pathLower, metadata.rev)
                    .download(outputStream)
            }

            // Tell android about the file
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(file)
            val data = Intent()
            data.putExtra("DropBox_PHOTOS", Uri.fromFile(file))
            mCallback?.onFinishProcess(data)
            mContext!!.sendBroadcast(intent)
            return file
        } catch (e: DbxException) {
            mException = e
        } catch (e: IOException) {
            mException = e
        }
        return null
    }


}
