package com.kv.ablecommunity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.kv.ablecommunity.adapter.CommentAdapter
import com.kv.ablecommunity.adapter.PostAdapter
import com.kv.ablecommunity.databinding.ActivityMainBinding
import com.kv.ablecommunity.firebase.FirestoreClass
import com.kv.ablecommunity.models.Comment
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
            R.id.myFollowers->{
                val intent = Intent(this,FollowersActivity::class.java)
                intent.putExtra("user",currentUser)
                startActivity(intent)
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
                    showCommentsDialog(post)
                }

                override fun onPressFollowbtn(
                    position: Int,
                    post: Post,
                    followbtn: AppCompatButton
                ) {
                    if(!currentUser.following.contains(post.users[0].id)){
                        followbtn.text = "Following"
                        followbtn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check,0,0,0)
                        followbtn.setTextColor(Color.WHITE)
                        followbtn.background =
                            ColorDrawable(ContextCompat.getColor(this@MainActivity,R.color.button_primary))


                    }
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


    fun postMenu(pos : Int,view : ImageView,menu : Int,post : Post){
        val popmenu = PopupMenu(this,view)
        popmenu.inflate(menu)
        val followedUserId = post.users[0].id
        val isFollowing = currentUser.following.contains(followedUserId)
        if(isFollowing){
            val followItem = popmenu.menu.findItem(R.id.follow_user_btn)
            followItem.title = "Following"
            followItem.setIcon(R.drawable.ic_check)
        }

        try {
            val fields = popmenu.javaClass.declaredFields
            for (field in fields) {
                if (field.name == "mPopup") {
                    field.isAccessible = true
                    val menuPopupHelper = field.get(popmenu)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.java)
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        popmenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when(item?.itemId){

                    R.id.delete_post_btn->{
                        showErrorDialog("Are you sure you want to delete this post?","Yes","No") {
                            showProgressDialog(resources.getString(R.string.please_wait))
                            FirestoreClass().removePost(this@MainActivity,currentPosts[pos].documentId)
                        }
                    }
                    R.id.edit_post->{
                        Toast.makeText(this@MainActivity,"Clicked",Toast.LENGTH_SHORT).show()
                    }
                    R.id.follow_user_btn->{
                        if(!isFollowing){
                            currentUser.following.add(followedUserId)
                            val userHashMap : HashMap<String,Any> = HashMap()
                            userHashMap[Constants.following] = currentUser.following
                            FirestoreClass().addfollowing(this@MainActivity,userHashMap)
                            item.title = "Following"
                            item.setIcon(R.drawable.ic_check)
                        }else{
                            showErrorDialog("Are you sure you want to unfollow?","Yes","No") {
                                currentUser.following.remove(followedUserId)
                                val userHashMap: HashMap<String, Any> = HashMap()
                                userHashMap[Constants.following] = currentUser.following
                                FirestoreClass().addfollowing(this@MainActivity, userHashMap)
                                item.title = "Follow"
                                item.setIcon(R.drawable.ic_add)
                                Toast.makeText(
                                    this@MainActivity,
                                    "You have unfollowed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                        }
                    }
                    R.id.report_user_btn->{
                        Toast.makeText(this@MainActivity,"Clicked report",Toast.LENGTH_SHORT).show()
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
    private fun showErrorDialog(warningtext : String,confirmText : String,denyText : String,confirmResponse : ()->Unit){
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.error_dialog_layout)
        dialog.findViewById<TextView>(R.id.warningText).text = warningtext
        dialog.findViewById<TextView>(R.id.confirmText).text = confirmText
        dialog.findViewById<TextView>(R.id.denyText).text = denyText
        dialog.findViewById<TextView>(R.id.confirmText).setOnClickListener {
            confirmResponse()
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.denyText).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()


    }

    private fun showCommentsDialog(post : Post){
        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.comments_view, null)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        //for fixing views when keyboard appears
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.setOnShowListener {
            //for updating parent view of bottomsheetlayout
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
        if(post.comments.isNotEmpty()){
            val adapter = CommentAdapter(this,post.comments)
            dialog.findViewById<RecyclerView>(R.id.commentsRv)!!.layoutManager = LinearLayoutManager(this)
            dialog.findViewById<RecyclerView>(R.id.commentsRv)!!.adapter = adapter
            dialog.findViewById<TextView>(R.id.noCommentsText)!!.visibility = View.GONE
        }
        dialog.findViewById<ImageView>(R.id.submitComment)!!.setOnClickListener {
            val comment = dialog.findViewById<EditText>(R.id.etComment)!!.text.toString()
            if(comment.isNotBlank()){
                showProgressDialog(resources.getString(R.string.please_wait))
                val commentObj= Comment(comment,currentUser,System.currentTimeMillis().toString())
                post.comments.add(commentObj)
                FirestoreClass().addComment(this,post)
                dialog.dismiss()
            }
        }
        dialog.show()
    }
    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_POST_REQUEST_CODE: Int = 12
    }
}