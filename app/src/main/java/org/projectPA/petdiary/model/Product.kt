package org.projectPA.petdiary.model

data class Product(
    val id: String,
    val petType: String,
    val category: String,
    val brandName: String,
    val productName: String,
    val description: String,
    val imageUrl: String?,
    val reviews: List<Review> = emptyList()
)
