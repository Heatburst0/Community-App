package com.kv.ablecommunity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kv.ablecommunity.R
import com.kv.ablecommunity.models.User
import de.hdodenhof.circleimageview.CircleImageView

class FollowersAdapter(
    private val context : Context,
    private val followers : ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

            return MyViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_follower,
                    parent,
                    false
                )
            )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val follower = followers[position]
        if(holder is MyViewHolder){
            holder.itemView.findViewById<TextView>(R.id.follow_user_name).text = follower.name
            holder.itemView.findViewById<TextView>(R.id.email_follow).text = follower.email
            Glide
                .with(context)
                .load(follower.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.itemView.findViewById<CircleImageView>(R.id.iv_user_image_follow                                        ))

        }
    }

    override fun getItemCount(): Int {
        return followers.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}