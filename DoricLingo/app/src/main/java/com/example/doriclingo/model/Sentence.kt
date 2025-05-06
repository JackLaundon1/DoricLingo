package com.example.doriclingo.model

//used for training screen
data class Sentence(
    val doric: String,
    val translation: String,
    val audioId: Int? = null
)
