package org.projectPA.petdiary.model

import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class User(
    @get:Exclude val id: String? = "",
    val name: String? = "",
    val address: String? = "",
    val email: String? = "",
    val gender: String? = "",
    val birthdate: String? = "",
    val bio: String? = "",
    val imageUrl: String? = "",
    val postCount: Int? = 0,
    val reviewCount: Int? = 0,
    val petCount: Int? = 0,
    val isModerator: Boolean = false
) : Serializable
