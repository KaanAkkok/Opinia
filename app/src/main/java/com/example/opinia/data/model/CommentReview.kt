package com.example.opinia.data.model

import com.google.firebase.firestore.DocumentId

data class CommentReview(
    @DocumentId
    val commentId: String = "",
    val courseId: String = "",
    val studentId: String = "",
    val rating: Int = 0,
    val comment: String = "",

    val timestamp: Long = System.currentTimeMillis() // yeni yazılan yorumu üstte göstermek için
)
