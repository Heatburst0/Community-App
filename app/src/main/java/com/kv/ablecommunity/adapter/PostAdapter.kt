package com.kv.ablecommunity.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.text.format.DateUtils
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kv.ablecommunity.MainActivity
import com.kv.ablecommunity.R
import com.kv.ablecommunity.firebase.FirestoreClass
import com.kv.ablecommunity.models.Post
import de.hdodenhof.circleimageview.CircleImageView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt


open class PostAdapter (private val context: Context,
    private var list: ArrayList<Post>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener : OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_post,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post =list[position]
        if(holder is MyViewHolder){
            val creator = post.users[0]
            Glide
                .with(context)
                .load(creator.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.itemView.findViewById<CircleImageView>(R.id.iv_user_image_post))
            holder.itemView.findViewById<TextView>(R.id.post_user_name).text=creator.name

            val likes = post.likes
            if(likes>0){
                holder.itemView.findViewById<TextView>(R.id.like_no).text = likes.toString()
            }

            holder.itemView.findViewById<TextView>(R.id.post_title).text=post.title
            holder.itemView.findViewById<TextView>(R.id.post_content).text=post.content
            val time= DateUtils.getRelativeTimeSpanString(post.timestamp.toLong()).toString()
            holder.itemView.findViewById<TextView>(R.id.timestamp_post).text=time
            if(post.likedby.contains(FirestoreClass().getCurrentUserID())){
                holder.itemView.findViewById<ImageView>(R.id.like_iv).setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_like_blue))
            }
            holder.itemView.findViewById<LinearLayout>(R.id.like_btn).setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, post,holder.itemView.findViewById<ImageView>(R.id.like_iv),holder.itemView.findViewById<TextView>(R.id.like_no))
                }
            }
            holder.itemView.findViewById<ImageView>(R.id.more_opt).setOnClickListener {
                var menu : Int=0
                menu = if(post.users[0].id==FirestoreClass().getCurrentUserID()){
                    R.menu.menu_post
                }else R.menu.menu_post2
                (context as MainActivity).postMenu(position,holder.itemView.findViewById<ImageView>(R.id.more_opt),menu,post)
            }
            val imageContainer = holder.itemView.findViewById<LinearLayout>(R.id.image_container)
            imageContainer.removeAllViews()
            imageContainer.visibility = View.GONE
            if(post.images.isNotEmpty()){
                val imageUris = post.images.map { Uri.parse(it) }
                bindImages(context,imageContainer,imageUris)
            }
            holder.itemView.findViewById<LinearLayout>(R.id.comments_iv).setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onShowComments(position, post)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    interface OnClickListener {
        fun onClick(position: Int, post : Post,view : ImageView,view2 : TextView)
        fun onShowComments(position: Int, post : Post)
        fun onPressFollowbtn(position: Int, post : Post,followbtn: AppCompatButton)
    }
    private inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnLongClickListener{
        init {
            view.setOnLongClickListener(this@MyViewHolder)
        }

        override fun onLongClick(v: View?): Boolean {

            return true
        }


    }
    private fun createImageView(context: Context, weight: Float = 0f): ImageView {
        return ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                if (weight > 0f) 0 else ViewGroup.LayoutParams.MATCH_PARENT,
                200.dp(context)
            ).apply {
                this.weight = weight
                marginEnd = 6.dp(context)
                topMargin = 6.dp(context)

            }
            background = AppCompatResources.getDrawable(context, R.drawable.bg_basic_stroke)
            scaleType = ImageView.ScaleType.CENTER_CROP

        }
    }

    private fun loadImage(uri: Uri, imageView: ImageView) {
        Glide
            .with(imageView.context)
            .load(uri) // Local URI works!
            .placeholder(R.drawable.ic_image_placeholder)
            .into(imageView)
    }

    fun Int.dp(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

    private fun bindImages(context: Context, imageContainer: LinearLayout, imageUris: List<Uri>) {
        imageContainer.removeAllViews()
        imageContainer.visibility = View.VISIBLE

        when (imageUris.size) {
            1 -> {
                val imageView = createImageView(context)
                loadImage(imageUris[0], imageView)
                imageContainer.addView(imageView)
            }

            2 -> {
                val row = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    weightSum = 2f
                }

                imageUris.forEach { uri ->
                    val imageView = createImageView(context, weight = 1f)
                    loadImage(uri, imageView)
                    row.addView(imageView)
                }

                imageContainer.addView(row)
            }

            3 -> {
                // First row with 2 images
                val topRow = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    weightSum = 2f
                }

                for (i in 0..1) {
                    val imageView = createImageView(context, weight = 1f)
                    loadImage(imageUris[i], imageView)
                    topRow.addView(imageView)
                }

                // Second row with full-width image
                val bottomRow = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                val fullWidthImage = createImageView(context)
                loadImage(imageUris[2], fullWidthImage)
                bottomRow.addView(fullWidthImage)

                imageContainer.addView(topRow)
                imageContainer.addView(bottomRow)
            }
        }
    }
}