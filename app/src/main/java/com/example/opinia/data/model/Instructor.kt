package com.example.opinia.data.model

import com.google.firebase.firestore.DocumentId

data class Instructor(
    @DocumentId
    val instructorId: String = "",
    val facultyId: String = "",
    val instructorName: String = "",
    val instructorEmail: String = "",
    val phoneNumber: String = "",
    val instructorTitle: String = "",
    val searchName: String = "",

    val departmentIds: List<String> = emptyList(),
    val givenCourseIds: List<String> = emptyList()
)
