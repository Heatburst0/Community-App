package com.kv.ablecommunity.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kv.ablecommunity.*
import com.kv.ablecommunity.models.Post
import com.kv.ablecommunity.models.User
import com.kv.ablecommunity.utils.Constants

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
    fun registerUser(activity: SignUpActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }
    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }
    fun loadUserData(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val loggedInUser = document.toObject(User::class.java)!!

                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is ProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity){
                    is SignInActivity ->{
                        activity.hideProgressDialog()
                    }
                    is ProfileActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }
    fun updateUserProfileData(activity: ProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")

                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }
    fun createPost(activity : CreatePostActivity,post : Post){
        mFireStore.collection(Constants.POSTS)
            .document()
            .set(post, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Post created successfully.")
                Toast.makeText(activity,"Post created successfully",Toast.LENGTH_SHORT).show()
                activity.postcreatedSuccessfully()
            }.addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }
    fun loadPosts(activity : MainActivity){
        mFireStore.collection(Constants.POSTS)
            .get()
            .addOnSuccessListener { it ->
                Log.e(activity.javaClass.simpleName, it.documents.toString())
                val posts : ArrayList<Post> = ArrayList()
                for(i in it.documents){
                    val post = i.toObject(Post::class.java)!!
                    post.documentId=i.id
                    posts.add(post)
                }
                activity.populatePosts(posts)
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while loading posts.", e)
            }
    }
    fun updatePosts(activity : MainActivity, post : Post){
        val hm : HashMap<String,Any> = HashMap()
        hm[Constants.POSTS] = post
        mFireStore.collection(Constants.POSTS)
            .document(post.documentId)
            .update(hm)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Post updated successfully.")
                activity.postUpdateSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Toast.makeText(activity,"Failed to update post",Toast.LENGTH_SHORT).show()
            }
    }

}