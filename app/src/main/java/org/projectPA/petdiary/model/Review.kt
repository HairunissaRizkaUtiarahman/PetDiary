package org.projectPA.petdiary.model

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class Review(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val userName: String = "",
    var userPhotoUrl: String = "default",
    val rating: Float = 0f,
    val usagePeriod: String = "",
    val reviewText: String = "",
    val recommend: Boolean = false,
    val reviewDate: Date = Date()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        productId = parcel.readString() ?: "",
        userId = parcel.readString() ?: "",
        userName = parcel.readString() ?: "",
        userPhotoUrl = parcel.readString() ?: "default",
        rating = parcel.readFloat(),
        usagePeriod = parcel.readString() ?: "",
        reviewText = parcel.readString() ?: "",
        recommend = parcel.readByte() != 0.toByte(),
        reviewDate = Date(parcel.readLong())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(productId)
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeString(userPhotoUrl)
        parcel.writeFloat(rating)
        parcel.writeString(usagePeriod)
        parcel.writeString(reviewText)
        parcel.writeByte(if (recommend) 1 else 0)
        parcel.writeLong(reviewDate.time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel): Review {
            return Review(parcel)
        }

        override fun newArray(size: Int): Array<Review?> {
            return arrayOfNulls(size)
        }
    }
}
