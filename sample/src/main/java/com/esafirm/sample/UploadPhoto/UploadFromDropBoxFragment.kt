package com.esafirm.sample.UploadPhoto

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import com.esafirm.sample.Utils.*
import com.esafirm.sample.databinding.UploadFromDropboxBindingBinding
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream


class UploadFromDropBoxFragment : Fragment() {
    private lateinit var binding: UploadFromDropboxBindingBinding
    private val APP_KEY = "4un8t7hjssl3j0o"
    private val PICKFILE_REQUEST_CODE = 1

    //init picaso client
    var dropboxClient: DbxClientV2? = null
    private var mSelectedFile: FileMetadata? = null

    //init picaso client
    private var sPicasso: Picasso? = null
    var madapter: DropBoxFileAdapter? = null
    var currentCardPosition: Int = 0
   // private var cards: ArrayList<CBigCardView.BigCardModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = UploadFromDropboxBindingBinding.inflate(inflater, container, false)

        binding.continueNext.setOnClickListener {
            startDropboxAuthorization()

        }


        sPicasso = Picasso.Builder(requireContext())
            .downloader(OkHttp3Downloader(requireContext()))
            .addRequestHandler(FileThumbnailRequestHandler())
            .build()


        madapter =
            DropBoxFileAdapter(sPicasso!!, object : DropBoxFileAdapter.Callback {
                override fun onFolderClicked(folder: FolderMetadata?) {
                    //startActivity(activity?.getIntent(this, folder.getPathLower()));
                }

                override fun onFileClicked(file: FileMetadata?) {
                    mSelectedFile = file
                    performWithPermissions(FileAction.DOWNLOAD)
                }
            })

        //init picaso client
        // PicassoClient.init(this, DbxRequestConfig.getClient())
        // val recyclerView = findViewById(R.id.files_list) as RecyclerView
//       var mFilesAdapter = DropBoxFileAdapter( object : DropBoxFileAdapter.Callback {
//          //  fun onFolderClicked(folder: FolderMetadata) {
////                startActivity(
////                    activity?.getIntent(
////                        re,
////                        folder.pathLower
////                    )
////                )
//           // }
//
////            fun onFileClicked(file: FileMetadata) {
////                mSelectedFile = file
////                performWithPermissions(FileAction.DOWNLOAD)
////            }
//
//           override fun onFolderClicked(folder: FolderMetadata?) {
//               TODO("Not yet implemented")
//           }
//
//           override fun onFileClicked(file: FileMetadata?) {
//               mSelectedFile = file
//               performWithPermissions(FileAction.DOWNLOAD)           }
//       })

        binding.disconnectDropbox.setOnClickListener {
            revokeDropboxAuthorization()
        }


        // binding.files.adapter=madapter
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        binding.files.adapter=adapter
//        fetchAccountInfo()
//        fetchDropboxFolder()
    }

    private fun performWithPermissions(action: FileAction) {
        if (hasPermissionsForAction(action)) {
            performAction(action)
            return
        }
        if (shouldDisplayRationaleForAction(action)) {
            AlertDialog.Builder(requireContext())
                .setMessage("This app requires storage access to download and upload files.")
                .setPositiveButton(
                    "OK"
                ) { dialog, which -> requestPermissionsForAction(action) }
                .setNegativeButton("Cancel", null)
                .create()
                .show()
        } else {
            requestPermissionsForAction(action)
        }
    }

    private fun performAction(action: FileAction) {
        when (action) {
            FileAction.UPLOAD -> launchFilePicker()
            FileAction.DOWNLOAD -> if (mSelectedFile != null) {
                downloadFile(mSelectedFile!!)
            } else {

            }
        }
    }

    private fun launchFilePicker() {
        // Launch intent to pick file for upload
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(
            intent,
            PICKFILE_REQUEST_CODE
        )
    }

    private fun startDropboxAuthorization() {
        // The client identifier is usually of the form "SoftwareName/SoftwareVersion".
        val clientIdentifier = "DropboxSampleAndroid/1.0.0"
        val requestConfig = DbxRequestConfig(clientIdentifier)

        // The scope's your app will need from Dropbox
        // Read more about Scopes here: https://developers.dropbox.com/oauth-guide#dropbox-api-permissions
        val scopes = listOf(
            "account_info.read",
            "files.content.write",
            "files.content.read",
            "files.metadata.read"
        )
        // Auth.startOAuth2PKCE(requireContext(),APP_KEY,requestConfig,scopes)
        Auth.startOAuth2PKCE(requireContext(), APP_KEY, requestConfig, scopes)
    }

    private fun revokeDropboxAuthorization() {
        val clientIdentifier = "DropboxSampleAndroid/1.0.0"
        val requestConfig = DbxRequestConfig(clientIdentifier)
        val credential = getLocalCredential()
        val dropboxClient = DbxClientV2(requestConfig, credential)
        val dropboxApi = DropboxApi(dropboxClient)
//         lifecycleScope.launch {
//            dropboxApi.revokeDropboxAuthorization()
//          }

        val sharedPreferences = activity?.getSharedPreferences(
            "dropbox-sample",
            AppCompatActivity.MODE_PRIVATE
        )
        sharedPreferences?.edit()?.remove("credential")?.apply()
        clearData()
        binding.continueNext.visibility = View.VISIBLE
        //binding.disconnectDropbox.visibility=View.GONE
        binding.iconContainer.visibility = View.VISIBLE
        binding.continueNext.isEnabled = true
        binding.files.visibility = View.GONE

    }

    override fun onResume() {
        super.onResume()

        //Check if we have an existing token stored, this will be used by DbxClient to make requests
        val localCredential: DbxCredential? = getLocalCredential()
        val credential: DbxCredential? = if (localCredential == null) {
            val credential = Auth.getDbxCredential() //fetch the result from the AuthActivity
            credential?.let {
                //the user successfully connected their Dropbox account!
                //startDropboxAuthorization()
                storeCredentialLocally(it)
                fetchAccountInfo()
                fetchDropboxFolder()
            }
            updateUi(credential)
            credential
        } else localCredential
        fetchDropboxFolder()
        updateUi(credential)
    }


    private fun updateUi(credential: DbxCredential?) {
        if (credential == null) {
            with(binding) {
                continueNext.visibility = View.VISIBLE
                disconnectDropbox.visibility = View.GONE
                iconContainer.visibility = View.VISIBLE
                continueNext.isEnabled = true
                files.visibility = View.GONE

            }
        } else {
            with(binding) {
                continueNext.visibility = View.GONE
                iconContainer.visibility = View.GONE
                disconnectDropbox.visibility = View.VISIBLE
                continueNext.isEnabled = true
                files.visibility = View.VISIBLE

            }
        }
    }

    private fun clearData() {
        madapter?.setFiles(emptyList())

        binding.continueNext.isEnabled = false
    }

    //deserialize the credential from SharedPreferences if it exists
    private fun getLocalCredential(): DbxCredential? {
        val sharedPreferences = activity?.getSharedPreferences(
            "dropbox-sample",
            AppCompatActivity.MODE_PRIVATE
        )
        val serializedCredential = sharedPreferences?.getString("credential", null) ?: return null
        return DbxCredential.Reader.readFully(serializedCredential)
    }

    //serialize the credential and store in SharedPreferences
    private fun storeCredentialLocally(dbxCredential: DbxCredential) {
        val sharedPreferences = activity?.getSharedPreferences(
            "dropbox-sample",
            AppCompatActivity.MODE_PRIVATE
        )
        sharedPreferences?.edit()?.putString("credential", dbxCredential.toString())?.apply()
    }

    private fun fetchAccountInfo() {
        val clientIdentifier = "DropboxSampleAndroid/1.0.0"
        val requestConfig = DbxRequestConfig(clientIdentifier)
        val credential = getLocalCredential()
        credential?.let {
            val dropboxClient = DbxClientV2(requestConfig, credential)
            val dropboxApi = DropboxApi(dropboxClient)
            lifecycleScope.launch {
                when (val response = dropboxApi.getAccountInfo()) {
                    is DropboxAccountInfoResponse.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            "Error getting account info!",
                            Toast.LENGTH_SHORT
                        ).show()
//                        binding.exceptionText.text =
//                            "type: ${response.exception.javaClass} + ${response.exception.localizedMessage}"
                    }
                    is DropboxAccountInfoResponse.Success -> {
                        val profileImageUrl = response.accountInfo.profilePhotoUrl

                    }
                }
            }
        }
    }

    private fun fetchDropboxFolder() {
        val clientIdentifier = "DropboxSampleAndroid/1.0.0"
        val requestConfig = DbxRequestConfig(clientIdentifier)
        val credential = getLocalCredential()
        credential?.let {
            dropboxClient = DbxClientV2(requestConfig, credential)
            val dropboxApi = DropboxApi(dropboxClient!!)

            lifecycleScope.launch {
                dropboxApi.getFilesForFolderFlow("/Photos").collect {
                    when (it) {
                        is GetFilesResponse.Failure -> {
                            Toast.makeText(
                                requireContext(),
                                "Error getting Dropbox files!",
                                Toast.LENGTH_SHORT
                            ).show()
//                            binding.exceptionText.text =
//                                "type: ${it.exception.javaClass} + ${it.exception.localizedMessage}"
                        }
                        is GetFilesResponse.Success -> {
                            madapter?.setFiles(it.result)
                            binding.files.adapter = madapter

                        }
                    }
                }
            }
        }
    }

    private fun selectFileForUpload() {
        contract.launch("image/*")
    }

    private fun uploadFile(fileName: String, inputStream: InputStream) {
        val clientIdentifier = "DropboxSampleAndroid/1.0.0"
        val requestConfig = DbxRequestConfig(clientIdentifier)
        val credential = getLocalCredential()
        credential?.let {
            val dropboxClient = DbxClientV2(requestConfig, credential)
            val dropboxApi = DropboxApi(dropboxClient)
            lifecycleScope.launch {
                // binding.uploadLoading.visibility = View.VISIBLE
                val response = dropboxApi.uploadFile(fileName, inputStream)
                //  binding.uploadLoading.visibility = View.GONE
                when (response) {
                    is DropboxUploadApiResponse.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            "Error uploading file",
                            Toast.LENGTH_SHORT
                        ).show()
//                        binding.exceptionText.text =
//                            "type: ${response.exception.javaClass} + ${response.exception.localizedMessage}"
                    }
                    is DropboxUploadApiResponse.Success -> {
                        Toast.makeText(
                            requireContext(),
                            "${response.fileMetadata.name} uploaded successfully!",
                            Toast.LENGTH_SHORT

                        ).show()
                    }
                }
            }
        }
    }

    private fun downloadFile(file: FileMetadata) {
        val dialog = ProgressDialog(requireContext())
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setCancelable(false)
        dialog.setMessage("Downloading")
        dialog.show()
        DownloadFileTask(requireContext(), dropboxClient, object : DownloadFileTask.Callback {
            override fun onDownloadComplete(result: File?) {
                dialog.dismiss()
               // result?.let { viewFileInExternalApp(it) }
            }

            override fun onFinishProcess(result: Intent) {
                activity?.setResult(3, result)
                activity?.finish()
            }


            override fun onError(e: Exception?) {
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "An error has occurred",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }).execute(file)
    }

    private fun viewFileInExternalApp(result: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val mime = MimeTypeMap.getSingleton()
        val ext = result.name.substring(result.name.indexOf(".") + 1)
        val type = mime.getMimeTypeFromExtension(ext)
        intent.setDataAndType(Uri.fromFile(result), type)

        // Check for a handler first to avoid a crash
        val manager: PackageManager = activity?.packageManager as PackageManager

        val resolveInfo = manager.queryIntentActivities(intent, 0)
        if (resolveInfo.size > 0) {
            startActivity(intent)
        }
    }


    private val openFileContract = object : ActivityResultContracts.GetContent() {
        override fun createIntent(context: Context, input: String): Intent {
            val intent = super.createIntent(context, input)
            val mimeTypes = arrayOf("image/*")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            return intent
        }
    }

    private val contract = registerForActivityResult(openFileContract) { uri ->
        if (uri != null) {
            if (uri.scheme == "content") {
                val name = uri.lastPathSegment
                val type = MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(activity?.contentResolver?.getType(uri))
                val inputStream = activity?.contentResolver?.openInputStream(uri)
                uploadFile("$name.$type", inputStream!!)
            }
        } else {
            Toast.makeText(requireContext(), "Error selecting file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasPermissionsForAction(action: FileAction): Boolean {
        for (permission in action.permissions) {
            val result = ContextCompat.checkSelfPermission(requireContext(), permission)
            if (result == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

    private fun shouldDisplayRationaleForAction(action: FileAction): Boolean {
        for (permission in action.permissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    permission
                )
            ) {
                return true
            }
        }
        return false
    }

    private fun requestPermissionsForAction(action: FileAction) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            action.permissions,
            action.getCode()
        )
    }

    fun newInstanceBack(activity: FragmentActivity) {
        activity.setResult(3)
        activity.finish()
    }

    private enum class FileAction(vararg permissions: String) {
        DOWNLOAD(Manifest.permission.WRITE_EXTERNAL_STORAGE), UPLOAD(Manifest.permission.READ_EXTERNAL_STORAGE);

        val permissions: Array<String> = permissions as Array<String>

        fun getCode(): Int {
            return ordinal
        }

        companion object {
            private val values: Array<FileAction> = FileAction.values()

            fun fromCode(code: Int): FileAction {
                require(!(code < 0 || code >= values.size)) { "Invalid FileAction code: $code" }
                return values[code]
            }
        }

    }
}

