package org.projectPA.petdiary.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Product(
    val id: String? = "",
    var petType: String = "",
    var category: String = "",
    var brandName: String = "",
    var productName: String = "",
    var desc: String = "",
    var imageUrl: String? = null,
    var averageRating: Double = 0.0,
    var totalRating: Double = 0.0,
    var reviewCount: Int = 0,
    var percentageOfUsers: Int = 0,
    var timeAdded: Timestamp? = Timestamp.now(),
    var lowercaseProductName: String = productName.lowercase(),
    var lowercaseBrandName: String = brandName.lowercase(),
    var uploaderName: String = "",
    var uploaderReviewDate: Timestamp? = Timestamp.now(),
    var uploaderReview: String = "",
    var usageUploader: String = "",
    var ratingUploader: Double = 0.0,
    var recommendUploader: Boolean = false
) : Serializable
