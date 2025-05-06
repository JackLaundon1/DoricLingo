package com.example.doriclingo.model

data class CourseProgress(
    //allows for the addition of multiple courses
    val course: String,
    val progress: Float,
    val lastSentenceID: Int
)
