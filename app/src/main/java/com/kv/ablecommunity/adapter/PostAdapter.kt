package com.kv.ablecommunity.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kv.ablecommunity.MainActivity
import com.kv.ablecommunity.R
import com.kv.ablecommunity.firebase.FirestoreClass
import com.kv.ablecommunity.models.Post
import kotlinx.android.synthetic.main.item_post.*
import kotlinx.android.synthetic.main.item_post.view.*

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
                .into(holder.itemView.iv_user_image_post)
            holder.itemView.post_user_name.text=creator.name

            val likes = post.likes
            if(likes>0){
                holder.itemView.like_no.text = likes.toString()
            }
            holder.itemView.post_title.text=post.title
            holder.itemView.post_content.text=post.content
            val time= DateUtils.getRelativeTimeSpanString(post.timestamp.toLong()).toString()
            holder.itemView.timestamp_post.text=time
            if(creator.name!=(context as MainActivity).mUserName){
                holder.itemView.more_opt.visibility = View.INVISIBLE
            }
            if(post.likedby.contains(FirestoreClass().getCurrentUserID())){
                holder.itemView.like_iv.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_like_blue))
            }
            holder.itemView.like_btn.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, post,holder.itemView.like_iv,holder.itemView.like_no)
                }
            }
            holder.itemView.more_opt.setOnClickListener {
                (context as MainActivity).postMenu(position,holder.itemView.more_opt)
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
    }
    private inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnLongClickListener{
        init {
            view.setOnLongClickListener(this@MyViewHolder)
        }

        override fun onLongClick(v: View?): Boolean {

            return true
        }


    }
}