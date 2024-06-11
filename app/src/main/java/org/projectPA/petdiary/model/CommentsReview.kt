package org.projectPA.petdiary.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class CommentsReview(
    val id: String = "",
    val reviewId: String = "",
    val userId: String = "",
    val text: String = "",
    val commentDate: Timestamp? = Timestamp.now(),
) : Serializable
