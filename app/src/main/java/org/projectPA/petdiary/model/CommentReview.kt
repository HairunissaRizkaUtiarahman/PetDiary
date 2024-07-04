package org.projectPA.petdiary.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class CommentReview(
    val id: String? = "",
    val reviewId: String = "",
    val userId: String = "",
    val commentText: String = "",
    val timeCommented: Timestamp? = Timestamp.now(),
    @get:Exclude val user: User? = User()
) : Serializable
