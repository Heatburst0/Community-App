package com.kv.ablecommunity.models

import android.os.Parcel
import android.os.Parcelable

data class Comment(
    val text : String="",
    val createdBy : User =User(),
    val timestamp : String=""
) : Parcelable {
    constructor(source : Parcel) : this(
        source.readString()!!,
        source.readParcelable<User>(User::class.java.classLoader)!!,
        source.readString()!!
    )
    override fun describeContents(): Int =0
    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(text)
        p0.writeParcelable(createdBy,0)
        p0.writeString(timestamp)
    }
    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Comment> = object : Parcelable.Creator<Comment> {
            override fun createFromParcel(source: Parcel): Comment = Comment(source)
            override fun newArray(size: Int): Array<Comment?> = arrayOfNulls(size)
        }
    }
}
