package org.projectPA.petdiary.model

data class Review(
    var reviewerName: String = "",
    var date: String = "",
    var rating: Double = 0.0,
    var text: String = "",
    var userImageUrl: String? = null  // Optional: URL for the reviewer's profile image
)
