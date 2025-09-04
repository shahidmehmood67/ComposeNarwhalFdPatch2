package com.cit.mycomposeapplication.models

import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable

/**
 * Model class for Quran Aya
 */
data class Aya(
    var text: String? = null,
    var name: String? = null,
    var nameEnglish: String? = null,
    var pageNumber: Int = 0,
    var ayaID: Int = 0,
    var suraID: Int = 0,
    var partID: Int = 0,
    var ayaRects: List<AyaRect>? = null
) : Parcelable {

    // --- Secondary constructors (all point directly to primary) ---
    constructor(pageNumber: Int, ayaID: Int, suraID: Int, text: String, name: String, nameEnglish: String) :
            this(text, name, nameEnglish, pageNumber, ayaID, suraID, 0, null)

    constructor(suraID: Int, ayaID: Int) :
            this(null, null, null, 0, ayaID, suraID, 0, null)

    constructor(pageNumber: Int, suraID: Int, ayaID: Int, ayaRects: List<AyaRect>) :
            this(null, null, null, pageNumber, ayaID, suraID, 0, ayaRects)

    constructor(pageNumber: Int, suraID: Int, ayaID: Int) :
            this(null, null, null, pageNumber, ayaID, suraID, 0, null)

    // --- Parcelable implementation ---
    private constructor(parcel: Parcel) : this(
        text = parcel.readString(),
        name = parcel.readString(),
        nameEnglish = parcel.readString(),
        pageNumber = parcel.readInt(),
        ayaID = parcel.readInt(),
        suraID = parcel.readInt(),
        partID = 0, // partID wasn't written, keep default
        ayaRects = parcel.createTypedArrayList(AyaRect.CREATOR)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeString(name)
        parcel.writeString(nameEnglish)
        parcel.writeInt(pageNumber)
        parcel.writeInt(ayaID)
        parcel.writeInt(suraID)
        parcel.writeTypedList(ayaRects)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Aya> {
        override fun createFromParcel(parcel: Parcel): Aya = Aya(parcel)
        override fun newArray(size: Int): Array<Aya?> = arrayOfNulls(size)
    }
}
