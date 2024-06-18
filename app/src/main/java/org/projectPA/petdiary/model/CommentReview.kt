package org.projectPA.petdiary.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class CommentReview(
    @get:Exclude val id: String? = "",
    val reviewId: String = "",
    val userId: String = "",
    val text: String = "",
    val commentDate: Timestamp? = Timestamp.now(),
    @get:Exclude val user: User? = User()
) : Serializable
