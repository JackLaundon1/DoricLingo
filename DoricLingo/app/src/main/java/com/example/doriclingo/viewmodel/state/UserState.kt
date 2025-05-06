package com.example.doriclingo.viewmodel.state

import com.example.doriclingo.room.entity.UserEntity

//data class to hold the user state
data class UserState(
    //list holds multiple users
    val users: List<UserEntity> = emptyList(),
    //holds name
    var name: String = "",
    //holds email
    var email: String = "",
    //boolean to hold the state of adding a user
    val isAddingUser: Boolean = false,
)
