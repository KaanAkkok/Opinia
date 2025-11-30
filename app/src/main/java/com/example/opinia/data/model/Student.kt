package com.example.opinia.data.model

import com.google.firebase.firestore.DocumentId

data class Student(
    @DocumentId
    val studentId: String = "", // Firebase authentication id'si ile aynı olacak
    val studentEmail: String = "",
    val studentName: String = "",
    val studentSurname: String = "",
    val studentYear: String = "",
    val facultyID: String = "",
    val departmentID: String = "",
    val studentProfileAvatar: String = "", //Avatar sınıfının key'ini tutacak

    val enrolledCourseIds: List<String> = emptyList(),
    val savedCommentReviewIds: List<String> = emptyList()
)