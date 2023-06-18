package com.kv.ablecommunity.models

import android.os.Parcel
import android.os.Parcelable

data class Post(
    val title : String="",
    val createdBy : String="",
    val content : String="",
    val timestamp : String="",
    val users : ArrayList<User> = ArrayList(),
    var documentId :String=""


) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.createTypedArrayList(User.CREATOR)!!,
        source.readString()!!

    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeString(createdBy)
        writeString(content)
        writeString(timestamp)
        writeTypedList(users)
        writeString(documentId)

    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Post> = object : Parcelable.Creator<Post> {
            override fun createFromParcel(source: Parcel): Post = Post(source)
            override fun newArray(size: Int): Array<Post?> = arrayOfNulls(size)
        }
    }
}