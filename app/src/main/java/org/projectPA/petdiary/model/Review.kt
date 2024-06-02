package org.projectPA.petdiary.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import java.io.Serializable


data class Review(
    val id: String? = "",
    val productId: String = "",
    val userId: String = "",
    val userName: String = "",
    var userPhotoUrl: String? = "default", // Make this field nullable
    val rating: Float = 0f,
    val usagePeriod: String = "",
    val reviewText: String = "",
    val recommend: Boolean = false,
    val reviewDate: Timestamp? = Timestamp.now(),
    @get:Exclude val product: Product? = Product()
) : Serializable
