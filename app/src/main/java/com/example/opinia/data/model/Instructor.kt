package com.example.opinia.data.model

import com.google.firebase.firestore.DocumentId

data class Instructor(
    @DocumentId
    val instructorId: String = "",
    val instructorName: String = "",
    val instructorEmail: String = "",
    val instructorTitle: String = "",
    val instructorOfficeHours: String = "",
    val instructorOfficeLocation: String = "",

    val givenCourseIds: List<String> = emptyList()
)
