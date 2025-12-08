package com.example.opinia.data.model

import com.google.firebase.firestore.DocumentId

data class Faculty(
    @DocumentId
    val facultyId: String = "",
    val facultyName: String = "",
)
