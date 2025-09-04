package com.cit.mycomposeapplication.models

import android.os.Parcel
import android.os.Parcelable


data class AyaRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(left)
        parcel.writeFloat(top)
        parcel.writeFloat(right)
        parcel.writeFloat(bottom)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<AyaRect> {
        override fun createFromParcel(parcel: Parcel): AyaRect = AyaRect(parcel)
        override fun newArray(size: Int): Array<AyaRect?> = arrayOfNulls(size)
    }
}
