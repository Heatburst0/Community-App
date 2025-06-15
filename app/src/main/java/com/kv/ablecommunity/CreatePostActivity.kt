package com.kv.ablecommunity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kv.ablecommunity.ProfileActivity
import com.kv.ablecommunity.adapter.ImagesSelectedAdapter
import com.kv.ablecommunity.databinding.ActivityCreatePostBinding
import com.kv.ablecommunity.firebase.FirestoreClass
import com.kv.ablecommunity.models.Post
import com.kv.ablecommunity.models.User
import com.kv.ablecommunity.utils.Constants

class CreatePostActivity : BaseActivity() {
    private lateinit var currentUser : User
    private lateinit var binding : ActivityCreatePostBinding
    private var selectedImages : ArrayList<String> = ArrayList()
    private var downloadedImagesURL : ArrayList<String> = ArrayList()

    val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris != null) {
            val allowedImages =3
            if(uris.size>3) launchToast("You can select only 3 images",this@CreatePostActivity)
            val filteredUris = uris.take(allowedImages-selectedImages.size)

            if(filteredUris.isNotEmpty()){
                selectedImages.addAll(filteredUris.map { it.toString() })
                showSelectedImages()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()
        if(intent.hasExtra(Constants.user_details)){
            currentUser = intent.getParcelableExtra<User>(Constants.user_details)!!
        }
        binding.post.setOnClickListener {
            createPost()
        }
        binding.addImagesBtn.setOnClickListener {
            if(selectedImages.size>=3){
                launchToast("You can select only 3 images",this@CreatePostActivity)
            }
           else checkAndRequestStoragePermission()
        }
    }
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCreatepost)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarCreatepost.setNavigationOnClickListener { onBackPressed() }
    }
    private fun createPost(){
        val title = binding.etPostTitle.text.toString().trim()
        val content = binding.etPostContent.text.toString().trim()
        if(validateForm(title,content)){
            showProgressDialog(resources.getString(R.string.please_wait))
            val users : ArrayList<User> = ArrayList()
            users.add(currentUser)
            val post = Post(
                title,
                currentUser.name,
                content,
                System.currentTimeMillis().toString(),
                users
            )
            if (selectedImages.isNotEmpty()) {
                uploadImages { uploadedUrls ->
                    post.images.addAll(uploadedUrls)
                    FirestoreClass().createPost(this, post)
                }
            } else {
                FirestoreClass().createPost(this, post)
            }
        }
    }

    fun postcreatedSuccessfully(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun validateForm(title : String,content : String): Boolean {
        return when {
            TextUtils.isEmpty(title) -> {
                showErrorSnackBar("Please enter title.")
                false
            }
            TextUtils.isEmpty(content) -> {
                showErrorSnackBar("Please enter content.")
                false
            }
            else -> {
                true
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryForImages()
            } else {
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun openGalleryForImages() {
        imagePickerLauncher.launch("image/*")
    }

    private fun checkAndRequestStoragePermission() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGalleryForImages()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                Constants.READ_STORAGE_PERMISSION_CODE
            )
        }
    }
    private fun showSelectedImages(){
        binding.imagesSelectedRv.post {
            binding.imagesSelectedRv.layoutManager = GridLayoutManager(this, 3)
            val adapter = ImagesSelectedAdapter(this, selectedImages)
            binding.imagesSelectedRv.adapter = adapter
            adapter.setOnClickListener(object : ImagesSelectedAdapter.OnClickListener{
                override fun onClose(pos: Int) {
                    selectedImages.removeAt(pos)
                    adapter.notifyDataSetChanged()
                }
            })
            binding.imagesSelectedRv.visibility = View.VISIBLE
        }
    }
    fun uploadImages(onUploadComplete: (List<String>) -> Unit) {
        val tempUrls = ArrayList<String>()
        var uploadedCount = 0

        for (uriString in selectedImages) {
            val uri = uriString.toUri()
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "POST_IMAGE" + System.currentTimeMillis() + "." +
                        Constants.getFileExtension(this@CreatePostActivity, uri)
            )

            sRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                        tempUrls.add(downloadUri.toString())
                        uploadedCount++

                        if (uploadedCount == selectedImages.size) {
                            onUploadComplete(tempUrls)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@CreatePostActivity,
                        "Upload failed: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    uploadedCount++
                    if (uploadedCount == selectedImages.size) {
                        onUploadComplete(tempUrls) // Partial upload case
                    }
                }
        }
    }



}