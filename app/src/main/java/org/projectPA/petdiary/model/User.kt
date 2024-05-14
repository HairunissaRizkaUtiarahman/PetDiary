package com.example.testproject.dataclass

import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class User(
    @get:Exclude val id: String? = "",
    val name: String? = "",
    val address: String? = "",
    val email: String? = "",
    val bio: String? = "",
    val imageUrl: String? = ""
) : Serializable
