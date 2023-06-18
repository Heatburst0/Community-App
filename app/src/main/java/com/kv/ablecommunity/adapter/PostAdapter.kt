package com.kv.ablecommunity.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kv.ablecommunity.R
import com.kv.ablecommunity.models.Post
import kotlinx.android.synthetic.main.item_post.view.*

open class PostAdapter (private val context: Context,
    private var list: ArrayList<Post>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            holder.itemView.post_title.text=post.title
            holder.itemView.post_content.text=post.content
            val time= DateUtils.getRelativeTimeSpanString(post.timestamp.toLong()).toString()
            holder.itemView.timestamp_post.text=time
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    private inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}