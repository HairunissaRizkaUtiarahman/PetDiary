package org.projectPA.petdiary.model

import java.util.Date

data class Product(
    var id: String = "",
    var petType: String = "",
    var category: String = "",
    var brandName: String = "",
    var productName: String = "",
    var description: String = "",
    var imageUrl: String? = null,
    var averageRating: Double = 0.0,
    var reviewCount: Int = 0,
    var percentageOfUsers: Int = 0,
    var createdAt: Date = Date(),
    var productNameLower: String = productName.lowercase(),
    var brandNameLower: String = brandName.lowercase()
)
