package com.example.doriclingo.model

//used to handle progress operations
sealed class ProgressEvent {
    //used to update progress
    data class UpdateProgress(val conversation: Float, val lastConversation: Int) : ProgressEvent()
    //used to save progress
    object SaveProgress : ProgressEvent()
}