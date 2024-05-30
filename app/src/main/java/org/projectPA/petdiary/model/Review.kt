package org.projectPA.petdiary.model

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class Review(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val userName: String = "",
    var userPhotoUrl: String? = "default", // Make this field nullable
    val rating: Float = 0f,
    val usagePeriod: String = "",
    val reviewText: String = "",
    val recommend: Boolean = false,
    val reviewDate: Date = Date()
)
