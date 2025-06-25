package com.kv.ablecommunity.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kv.ablecommunity.R
import com.kv.ablecommunity.models.Comment

class CommentAdapter(
    private val context : Context,
    private val comments: List<Comment>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_comment,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if(holder is MyViewHolder){
            val comment = comments[position]
            holder.itemView.findViewById<TextView>(R.id.comment).text = comment.text
            holder.itemView.findViewById<TextView>(R.id.post_user_name).text = comment.createdBy.name
            val time= DateUtils.getRelativeTimeSpanString(comment.timestamp.toLong()).toString()
            holder.itemView.findViewById<TextView>(R.id.timestamp_post).text = time
            Glide
                .with(context)
                .load(comment.createdBy.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.findViewById(R.id.iv_user_image_comment))
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}