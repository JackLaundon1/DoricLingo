package com.example.doriclingo.interfaces

//used to perform user operations e.g. adding a user
sealed interface UserEvent{
    object SaveUser: UserEvent
    data class SetName(val name: String): UserEvent
    data class SetEmail(val email: String): UserEvent
    object showDialog: UserEvent
    object HideDialog: UserEvent
    object DeleteUser : UserEvent

}