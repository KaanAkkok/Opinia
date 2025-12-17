package com.example.opinia.data.model

import com.google.firebase.firestore.DocumentId
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CommentReview(
    @DocumentId
    val commentId: String = "",
    val courseId: String = "",
    val studentId: String = "",
    val rating: Int = 0,
    val comment: String = "",

    val timestamp: Long = System.currentTimeMillis() // yeni yazılan yorumu üstte göstermek için
) {
    val formattedDate: String
        get() {
            val date = Date(timestamp)
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return format.format(date)
        }
}
