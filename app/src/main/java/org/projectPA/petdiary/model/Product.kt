package org.projectPA.petdiary.model

data class Product(
    var id: String = "",
    var petType: String = "",
    var category: String = "",
    var brandName: String = "",
    var productName: String = "",
    var description: String = "",
    var imageUrl: String? = null
)
