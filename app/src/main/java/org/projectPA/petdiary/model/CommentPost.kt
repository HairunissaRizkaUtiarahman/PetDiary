package com.example.testproject.dataclass

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import org.projectPA.petdiary.model.User
import java.io.Serializable

data class CommentPost(
    @get:Exclude val id: String? = "",
    val comment: String? = "",
    val timestamp: Timestamp? = Timestamp.now(),
    @get:Exclude val user: User? = User()
) : Serializable