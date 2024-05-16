package org.projectPA.petdiary.model

import java.util.Date

data class Review(
    var id: String = "",
    var productId: String = "",
    var userId: String? = null,
    var userName: String? = null,
    var userPhotoUrl: String? = null,
    var rating: Float = 0.0f,
    var usagePeriod: String = "",
    var reviewText: String = "",
    var recommend: Boolean = false,
    var reviewDate: Date = Date(),
)
