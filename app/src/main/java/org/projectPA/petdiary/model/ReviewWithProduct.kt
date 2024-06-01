package org.projectPA.petdiary.model

data class ReviewWithProduct(
    val review: Review = Review(),
    val product: Product = Product()
)
