package com.kv.ablecommunity.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.kv.ablecommunity.R
import com.kv.ablecommunity.models.Post
import androidx.core.net.toUri

class ImagesSelectedAdapter(
    private val context : Context,
    private val selectedImages: ArrayList<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener : OnClickListener?= null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_image_selected,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val selectedImage = selectedImages[position]
        if(holder is MyViewHolder){
            holder.itemView.findViewById<TextView>(R.id.imageName).text = selectedImage.truncate(10)
            holder.itemView.findViewById<ImageView>(R.id.ivSelectedImage).setImageURI(selectedImage.toUri())
            holder.itemView.findViewById<ImageView>(R.id.cancelImage).setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClose(position)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return selectedImages.size
    }
    fun String.truncate(maxLength: Int): String {
        return if (this.length > maxLength) this.take(maxLength) + "…" else this
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    interface OnClickListener {
        fun onClose(pos : Int)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}