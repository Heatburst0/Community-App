package com.kv.ablecommunity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.kv.ablecommunity.adapter.PostAdapter
import com.kv.ablecommunity.databinding.ActivityMainBinding
import com.kv.ablecommunity.firebase.FirestoreClass
import com.kv.ablecommunity.models.Post
import com.kv.ablecommunity.models.User
import com.kv.ablecommunity.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var mUserName: String
    private lateinit var currentUser : User
    private lateinit var currentPosts : ArrayList<Post>
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        binding.navView.setNavigationItemSelectedListener(this)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this@MainActivity)
        binding.appBarMain.createPost.setOnClickListener {
            val intent = Intent(this@MainActivity,CreatePostActivity::class.java)
            intent.putExtra(Constants.user_details,currentUser)
            startActivityForResult(intent, CREATE_POST_REQUEST_CODE)
        }
    }
    private fun setupActionBar() {

        setSupportActionBar(binding.appBarMain.toolbarMainActivity)
        binding.appBarMain.toolbarMainActivity.setNavigationIcon(R.drawable.ham_c_2)

        binding.appBarMain.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    /**
     * A function for opening and closing the Navigation Drawer.
     */
    private fun toggleDrawer() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {

                startActivityForResult(
                    Intent(this@MainActivity, ProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE
                )
            }
            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()
                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            // Get the user updated details.
            FirestoreClass().loadUserData(this@MainActivity)
        }else if (resultCode == Activity.RESULT_OK
            && requestCode == CREATE_POST_REQUEST_CODE) {
            FirestoreClass().loadPosts(this@MainActivity)
        }
        else {
            Log.e("Cancelled", "Cancelled")
        }
    }
    fun updateNavigationUserDetails(user: User) {

        hideProgressDialog()
        currentUser=user
        mUserName = user.name

        val headerView = binding.navView.getHeaderView(0)
        val navUserImage = headerView.findViewById<ImageView>(R.id.iv_user_image)
        Glide
            .with(this@MainActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)
        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        navUsername.text = user.name
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadPosts(this@MainActivity)
    }
    fun populatePosts(posts : ArrayList<Post>){
        hideProgressDialog()
        if(posts.size>0){
            currentPosts=posts
            binding.appBarMain.contentMain.rvPosts.visibility= View.VISIBLE
            binding.appBarMain.contentMain.txtNoposts.visibility = View.GONE

            binding.appBarMain.contentMain.rvPosts.layoutManager=LinearLayoutManager(this@MainActivity)
            binding.appBarMain.contentMain.rvPosts.setHasFixedSize(true)
            val adapter = PostAdapter(this@MainActivity,posts)
            binding.appBarMain.contentMain.rvPosts.adapter=adapter
            adapter.setOnClickListener(object:
            PostAdapter.OnClickListener{
                override fun onClick(position: Int, post: Post,view : ImageView,view2 : TextView) {
                    val likedby = post.likedby
                    var found  = false
                    var ind =-1
                    for(i in likedby.indices){
                        if(likedby[i]==currentUser.id){
                            ind=i
                            found=true
                        }
                    }
                    if(!found){
                        view.setImageDrawable(AppCompatResources.getDrawable(this@MainActivity,R.drawable.ic_like_blue))
                        post.likes++
                        post.likedby.add(currentUser.id)
                    }else{
                        view.setImageDrawable(AppCompatResources.getDrawable(this@MainActivity,R.drawable.ic_like))
                        post.likes--
                        post.likedby.removeAt(ind)
                    }
                    view2.text = post.likes.toString()
                    FirestoreClass().updatePosts(this@MainActivity,post)
                }

                override fun onShowComments(
                    position: Int,
                    post: Post
                ) {
                    showCommentsDialog()
                }

            })

        }else{
            binding.appBarMain.contentMain.rvPosts.visibility= View.GONE
            binding.appBarMain.contentMain.txtNoposts.visibility = View.VISIBLE
        }
    }
    fun postUpdateSuccess(){
        hideProgressDialog()

    }


    /** Context Menu Trial**/


    fun postMenu(pos : Int,view : ImageView){
        val popmenu = PopupMenu(this,view)
        popmenu.inflate(R.menu.menu_post)
        popmenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when(item?.itemId){

                    R.id.delete_post_btn->{
                        showProgressDialog(resources.getString(R.string.please_wait))
                        FirestoreClass().removePost(this@MainActivity,currentPosts[pos].documentId)
                    }
                    R.id.edit_post->{
                        Toast.makeText(this@MainActivity,"Clicked",Toast.LENGTH_SHORT).show()
                    }
                }
                return true

            }

        })
        popmenu.show()

    }
    fun removePostSuccess(){
        hideProgressDialog()
        Toast.makeText(this,"Post removed Successfully!!",Toast.LENGTH_SHORT).show()
    }

    private fun showCommentsDialog(){
        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.comments_view, null)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        dialog.setOnShowListener {
            val bottomSheet = (dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)) as FrameLayout
            bottomSheet.setBackgroundColor(Color.TRANSPARENT)
            bottomSheet.let {
                // Make bottom sheet height match parent
                val layoutParams = it.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.layoutParams = layoutParams

                // Expand and make draggable
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isDraggable = true
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }
    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_POST_REQUEST_CODE: Int = 12
    }
}