package com.example.doriclingo.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//entity creates an SQLite table
@Entity(tableName = "UserDetails")
data class UserEntity(
    //name
    val name: String,
    //email
    val email: String,
    //primary key is not set to auto generate as this will be the firebase user ID
    @PrimaryKey(autoGenerate = false)
    val id: String
)
