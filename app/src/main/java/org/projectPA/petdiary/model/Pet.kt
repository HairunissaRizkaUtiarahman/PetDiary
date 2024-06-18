package org.projectPA.petdiary.model
import com.google.firebase.Timestamp
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
    val timestamp: Timestamp? = Timestamp.now(),
    @get:Exclude val user: User? = User()
) : Serializable
