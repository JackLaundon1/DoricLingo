package com.example.doriclingo.room.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserProgress")
data class ProgressEntity(
    //primary key is not set to auto generate as this will be the firebase user ID
    @PrimaryKey val id: String,
    //progress data
    val conversation: Float,
    //last conversation data
    val lastConversation: Int,
)
