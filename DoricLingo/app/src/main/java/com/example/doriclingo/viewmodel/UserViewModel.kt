package com.example.doriclingo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doriclingo.room.dao.ProgressDao
import com.example.doriclingo.room.dao.UserDao
import com.example.doriclingo.room.entity.UserEntity
import com.example.doriclingo.interfaces.UserEvent
import com.example.doriclingo.room.entity.ProgressEntity
import com.example.doriclingo.viewmodel.state.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel(
    //accesses the user dao and progress dao
    private val dao: UserDao,
    private val progressDao: ProgressDao
) : ViewModel() {

    //gets the current user ID from firebase
    private val _currentUserId = MutableStateFlow<String?>(FirebaseAuth.getInstance().currentUser?.uid)

    init {
        //detects firebase state changes
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            _currentUserId.value = auth.currentUser?.uid
        }
    }

    //observes room db for users tied to the current user ID
    private val _users: Flow<List<UserEntity>> = _currentUserId.flatMapLatest { userId ->
        userId?.let { dao.getUser(it) } ?: MutableStateFlow(emptyList())
    }

    //combines UI state and user list to create a single state
    private val _state = MutableStateFlow(UserState())
    val state = combine(_state, _users) { state, users ->
        state.copy(users = users)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserState())

    //handles UI events
    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.DeleteUser -> {
                //firebase userID is required as firebase manages authentication
                val userID = FirebaseAuth.getInstance().currentUser?.uid

                viewModelScope.launch {
                    if (userID != null) {
                        //deletes from room
                        dao.deleteUser(userID)
                        progressDao.deleteProgress(userID)

                        //deletes from firebase
                        FirebaseAuth.getInstance().currentUser?.delete()?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                FirebaseAuth.getInstance().signOut()
                            }
                        }
                    }
                }
            }

            UserEvent.HideDialog -> {
                //hides the dialog
                _state.update { it.copy(isAddingUser = false) }
            }

            UserEvent.SaveUser -> {
                val name = state.value.name
                val email = state.value.email

                //returns if either name or email field is empty
                if (name.isBlank() || email.isBlank()) {
                    return
                }

                val auth = FirebaseAuth.getInstance()
                val currentUser: FirebaseUser? = auth.currentUser
                val currentID = currentUser?.uid

                //creates a new user for room db
                val user = currentID?.let {
                    UserEntity(
                        name = name,
                        email = email,
                        id = it
                    )
                }

                val firestore = FirebaseFirestore.getInstance()

                //attempts to pull user data from firebase to store in room
                if (currentID != null) {
                    firestore.collection("progress")
                        .document(currentID)
                        .get()
                        .addOnSuccessListener { document ->
                            //extracts progress from firebase
                            val conversation = document.getDouble("conversation")?.toFloat() ?: 0f
                            val lastConversation = document.getLong("last_conversation")?.toInt() ?: 0

                            val progress = ProgressEntity(
                                id = currentID,
                                conversation = conversation,
                                lastConversation = lastConversation
                            )
                            //save user and progress to room db
                            viewModelScope.launch {
                                if (user != null) {
                                    dao.insertUser(user)
                                }
                                progressDao.insertProgress(progress)
                            }
                        }
                        .addOnFailureListener {
                            //if firebase fetch fails, store default progress
                            viewModelScope.launch {
                                if (user != null) {
                                    dao.insertUser(user)
                                }
                                val defaultProgress = ProgressEntity(currentID, 0f, 0) // Default lastConversation
                                progressDao.insertProgress(defaultProgress)
                            }
                        }
                }

                //resets UI fields
                _state.update {
                    it.copy(
                        isAddingUser = false,
                        name = "",
                        email = "",
                    )
                }
            }

            is UserEvent.SetEmail -> {
                //updates the current email input
                _state.update { it.copy(email = event.email) }
            }

            is UserEvent.SetName -> {
                //updates the current name input
                _state.update { it.copy(name = event.name) }
            }

            UserEvent.showDialog -> {
                //shows dialog
                _state.update { it.copy(isAddingUser = true) }
            }
        }
    }
}

