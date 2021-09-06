package org.abubaker.happyplaces.models

import android.os.Parcel
import android.os.Parcelable

/**
 * A Data Model Class for Happy Place details. We will you this data class in all over the project even when
 * dealing with local SQLite database.
 */
data class HappyPlaceModel(
    val id: Int,
    val title: String?,
    val image: String?,
    val description: String?,
    val date: String?,
    val location: String?,
    val latitude: Double,
    val longitude: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(description)
        parcel.writeString(date)
        parcel.writeString(location)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HappyPlaceModel> {
        override fun createFromParcel(parcel: Parcel): HappyPlaceModel {
            return HappyPlaceModel(parcel)
        }

        override fun newArray(size: Int): Array<HappyPlaceModel?> {
            return arrayOfNulls(size)
        }
    }
}

// ) : Serializable

/**
 * Often, when we develop applications, we have to transfer data from one Activity to another.
 * Of course, we can not do that directly. The data we want to transfer must be included
 * into a corresponding Intent object. To do that, our object must be either:
 *
 * 1. Serializable (Java Based - SLOW Performance)
 * - Uses Reflection
 * - Causes lot of Garbage Collection
 *
 * 2. Parcelable (Native Android SDK - Fast Performance)
 * - No Reflection
 * - Focused
 */

