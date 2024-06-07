package org.projectPA.petdiary.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import java.io.Serializable


data class Review(
    var id: String? = "",
    var productId: String = "",
    var userId: String = "",
    var rating: Float = 0f,
    var usagePeriod: String = "",
    var reviewText: String = "",
    var recommend: Boolean = false,
    val reviewDate: Timestamp? = Timestamp.now(),
    @get:Exclude val product: Product? = Product(),
    @get:Exclude var user: User? = User()
) : Serializable
