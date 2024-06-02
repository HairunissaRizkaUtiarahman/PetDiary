package org.projectPA.petdiary.model

import com.google.firebase.Timestamp
import java.io.Serializable
import java.util.Date

data class Product(
    val id: String? = "",
    var petType: String = "",
    var category: String = "",
    var brandName: String = "",
    var productName: String = "",
    var description: String = "",
    var imageUrl: String? = null,
    var averageRating: Double = 0.0,
    var reviewCount: Int = 0,
    var percentageOfUsers: Int = 0,
    var createdAt: Timestamp? = Timestamp.now(),
    var productNameLower: String = productName.lowercase(),
    var brandNameLower: String = brandName.lowercase()
) : Serializable
