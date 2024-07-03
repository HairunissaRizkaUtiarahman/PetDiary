package org.projectPA.petdiary.model

import java.io.Serializable
import java.util.Date

data class Article(
    val articleId: String = "",
    val title: String = "",
    val category: String = "",
    val timeAdded: Date = Date(),
    val articleText: String = "",
    var imageUrl: String = "",
    val sourceUrl: String = ""
) : Serializable