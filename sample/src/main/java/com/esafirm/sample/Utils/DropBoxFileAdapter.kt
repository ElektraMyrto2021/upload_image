package com.esafirm.sample.Utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import com.dropbox.core.v2.files.Metadata
import com.esafirm.sample.R
import com.squareup.picasso.Picasso

class DropBoxFileAdapter(picasso: Picasso, callback: Callback) :
    RecyclerView.Adapter<DropBoxFileAdapter.MetadataViewHolder?>() {
    private var mFiles: List<Metadata>? = null
    private val mPicasso: Picasso
    private val mCallback: Callback

    fun setFiles(files: List<Metadata>?) {
        mFiles = files
        notifyDataSetChanged()
    }

    interface Callback {
        fun onFolderClicked(folder: FolderMetadata?)
        fun onFileClicked(file: FileMetadata?)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MetadataViewHolder {
        val context = viewGroup.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_file, viewGroup, false)
        return MetadataViewHolder(view)
    }

    override fun onBindViewHolder(metadataViewHolder: MetadataViewHolder, i: Int) {
        metadataViewHolder.bind(mFiles!![i])
    }

    override fun getItemId(position: Int): Long {
        return mFiles!![position].pathLower.hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return mFiles?.size!!
    }


    inner class MetadataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val mTextView: TextView
        private val mImageView: ImageView
        private var mItem: Metadata? = null
        override fun onClick(v: View) {
            if (mItem is FolderMetadata) {
                mCallback.onFolderClicked(mItem as FolderMetadata?)
            } else if (mItem is FileMetadata) {
                mCallback.onFileClicked(mItem as FileMetadata?)
            }
        }

        fun bind(item: Metadata) {
            mItem = item
            mTextView.text = mItem!!.name+" "+ mItem!!.pathDisplay
           // mImageView.setImageDrawable()
            // Load based on file path
            // Prepending a magic scheme to get it to
            // be picked up by DropboxPicassoRequestHandler
            var fileThumbnailRequestHandler = FileThumbnailRequestHandler()
            if (item is FileMetadata) {
                val mime = MimeTypeMap.getSingleton()
                val ext: String = item.getName().substring(item.getName().indexOf(".") + 1)
                val type = mime.getMimeTypeFromExtension(ext)
                if (type != null && type.startsWith("image/")) {

//  Picasso.get().
// load("https://images.unsplash.com/photo-1650468854655-8638161bfdc4?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwzMTIyOTd8MHwxfGFsbHwzfHx8fHx8Mnx8MTY1MDU0NzYwMA&ixlib=rb-1.2.1&q=80&w=1080")
//      .into(mImageView)
                    val uri=fileThumbnailRequestHandler.buildPicassoUri(item)
                    mPicasso.load(uri)
                       .placeholder(R.drawable.ic_file)
                        .error(R.drawable.ic_file)
                        .into(mImageView)

//              val uri=fileThumbnailRequestHandler.buildPicassoUri(item)
//                    Glide.with(mImageView)
//                        .load(uri)
//                        .into(mImageView)
                } else {
                    val uri=fileThumbnailRequestHandler.buildPicassoUri(item)
                    mPicasso.load(uri)
                        .placeholder(R.drawable.ic_file)
                        .error(R.drawable.ic_file)
                        .into(mImageView)
//                    Picasso.get().
//                    load("https://images.unsplash.com/photo-1650468854655-8638161bfdc4?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwzMTIyOTd8MHwxfGFsbHwzfHx8fHx8Mnx8MTY1MDU0NzYwMA&ixlib=rb-1.2.1&q=80&w=1080")
//                        .into(mImageView)
//                    mPicasso.load(R.drawable.ic_file)
//                        .noFade()
//                        .into(mImageView)
                }
            } else if (item is FolderMetadata) {

               // Picasso.get().load(fileThumbnailRequestHandler.buildPicassoUri(item))
                    // .placeholder(R.drawable.ic_file)
                    //.error(R.drawable.ic_file)
                  //  .into(mImageView)
//                Picasso.get().
//                load("https://images.unsplash.com/photo-1650468854655-8638161bfdc4?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwzMTIyOTd8MHwxfGFsbHwzfHx8fHx8Mnx8MTY1MDU0NzYwMA&ixlib=rb-1.2.1&q=80&w=1080")
//                    .into(mImageView)
         mPicasso.load(R.drawable.ic_folder)
                   .noFade()
                 .into(mImageView)
            }
        }

        init {
            mImageView = itemView.findViewById<View>(R.id.file_icon) as ImageView
            mTextView = itemView.findViewById<View>(R.id.file_name) as TextView
            itemView.setOnClickListener(this)
        }
    }

    init {
        mPicasso = picasso
        mCallback = callback
    }


}