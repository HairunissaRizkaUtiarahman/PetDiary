package org.projectPA.petdiary.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Post(
    @get:Exclude val id: String? = "",
    val desc: String? = "",
    val imageUrl: String? = "",
    val timePosted: Timestamp? = Timestamp.now(),
    val isDeleted: Boolean? = false,
    val like: Like? = Like(),
    val likeCount: Int? = 0,
    val commentCount: Int? = 0,
    @get:Exclude val user: User? = User()
) : Serializable