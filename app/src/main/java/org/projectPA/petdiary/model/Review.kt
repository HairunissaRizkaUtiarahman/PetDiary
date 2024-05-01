package org.projectPA.petdiary.model

data class Review(
    val id: Int,
    val productId: Int,
    val user: String,
    val review: String,
    val rating: Int
)