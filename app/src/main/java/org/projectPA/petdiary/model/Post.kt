package org.projectPA.petdiary.model

import com.example.testproject.dataclass.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Post(
    @get:Exclude val id: String? = "",
    val desc: String? = "",
    val imageUrl: String? = "",
    val timestamp: Timestamp? = Timestamp.now(),
    val isDeleted: Boolean? = false,
    @get:Exclude val user: User? = User()
) : Serializable
