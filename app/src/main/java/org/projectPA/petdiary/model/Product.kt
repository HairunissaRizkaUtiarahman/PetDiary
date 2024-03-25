package org.projectPA.petdiary.model

import android.media.Rating

data class Product(val name: String, val brand: String, val imageRes: Int, val reviewsCount: Int, val rating: Int)
