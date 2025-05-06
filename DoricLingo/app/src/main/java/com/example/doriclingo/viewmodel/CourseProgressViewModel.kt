package com.example.doriclingo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.doriclingo.model.CourseProgress
import com.example.doriclingo.repositories.ProgressRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourseProgressViewModel(application: Application) : AndroidViewModel(application) {

    //accesses the progress repository
    private val repository = ProgressRepository(application)

    //private stateflow and can only be updated inside the view model
    private val _courseProgress = MutableStateFlow<List<CourseProgress>>(emptyList())
    //public stateflow exposed to the UI
    val courseProgress: StateFlow<List<CourseProgress>> = _courseProgress

    // Stores the last sentence ID for conversation course
    private val _lastConversationID = MutableStateFlow(0)

    //called when the viewmodel is created and begins fetching data
    init {
        fetchProgress()
    }

    private fun fetchProgress() {
        //gets current user ID
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        viewModelScope.launch {
            repository.getUserProgress(uid).collect { progressList ->
                //collects data as a flow from the repository
                _courseProgress.value = progressList
                if (progressList.isNotEmpty()) {
                    //updates the last sentence ID
                    _lastConversationID.value = progressList[0].lastSentenceID
                }
            }
        }
    }



    fun resetProgress(onResult: (Boolean, String?) -> Unit){
        //gets the current user ID
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        viewModelScope.launch {
            //calls the reset progress function in the repository
            repository.resetProgress(uid)
            //gets an instance of firestore
            val firestore = Firebase.firestore
            //sets data to 0
            val progress = 0f
            val lastSentence = 0
            val progressData = mapOf(
                "conversation" to progress,
                "last_conversation" to lastSentence
            )
            //updates the progress in firestore, setting all values to 0
            firestore.collection("progress").document(uid)
                .update(progressData)
                //returns true or false depending on if the task is successful
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, task.exception?.localizedMessage)
                    }
                }
        }
    }


}
