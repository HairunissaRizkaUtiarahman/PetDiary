package org.projectPA.petdiary.model

data class Product(
    var id: String = "",
    var petType: String = "",
    var category: String = "",
    var brandName: String = "",
    var productName: String = "",
    var productNameLower: String = "",
    var description: String = "",
    var imageUrl: String? = null,
    var averageRating: Double = 0.0,
    var reviewCount: Int = 0,
    var percentageOfUsers: Int = 0
)
