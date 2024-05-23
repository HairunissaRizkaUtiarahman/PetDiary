package org.projectPA.petdiary.model

import com.example.testproject.dataclass.User
import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Pet(
    @get:Exclude val id: String? = "",
    val name: String? = "",
    val type: String? = "",
    val gender: String? = "",
    val age: Int? = 0,
    val desc: String? = "",
    val imageUrl: String? = "",
    val isDeleted: Boolean? = false,
    @get:Exclude val user: User? = User()
) : Serializable
