package org.projectPA.petdiary.model

data class Product(
    val name: String,
    val brand: String,
    val imageResId: Int,
    val rating: Float,
    val reviewCount: Int
)
