package com.example.opinia.data.model

import com.google.firebase.firestore.DocumentId

data class Course(
    @DocumentId
    val courseId: String = "",
    val courseCode: String = "",
    val courseName: String = "",
    val facultyId: String = "",
    val akts: Int = 0,
    val credits: Int = 0,
    val searchCode: String = "",

    val departmentIds: List<String> = emptyList(),
    val instructorIds: List<String> = emptyList()
)
