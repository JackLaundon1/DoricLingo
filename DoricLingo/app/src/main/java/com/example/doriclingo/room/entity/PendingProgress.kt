package com.example.doriclingo.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//sets up table for pending progress in room db
@Entity(tableName = "PendingProgress")
data class PendingProgress(
    //primary key is not set to auto generate as this will be the firebase user ID
    @PrimaryKey var id: String,
    //progress data
    val progress: Float,
    //last sentence data
    val lastSentence: Int,
)
