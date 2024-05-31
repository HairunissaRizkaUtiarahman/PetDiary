package org.projectPA.petdiary.model

import java.io.Serializable
import java.util.Date

data class Article(
    val id: String = "",
    val tittle: String = "",
    val category: String = "",
    val date: Date = Date(),
    val body: String = "",
    val imageUrl: String = "",
    val sourceUrl: String = ""
) : Serializable