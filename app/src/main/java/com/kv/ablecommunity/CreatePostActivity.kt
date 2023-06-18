package com.kv.ablecommunity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import com.kv.ablecommunity.firebase.FirestoreClass
import com.kv.ablecommunity.models.Post
import com.kv.ablecommunity.models.User
import com.kv.ablecommunity.utils.Constants
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_sign_in.*

class CreatePostActivity : BaseActivity() {
    private lateinit var currentUser : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()
        if(intent.hasExtra(Constants.user_details)){
            currentUser = intent.getParcelableExtra<User>(Constants.user_details)!!
        }
        post.setOnClickListener {
            createPost()
        }
    }
    private fun setupActionBar() {

        setSupportActionBar(toolbar_createpost)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_createpost.setNavigationOnClickListener { onBackPressed() }
    }
    private fun createPost(){
        val title = et_post_title.text.toString().trim()
        val content = et_post_content.text.toString().trim()
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
            FirestoreClass().createPost(this,post)
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


}