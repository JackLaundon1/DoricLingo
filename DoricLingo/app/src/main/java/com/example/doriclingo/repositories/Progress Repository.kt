package com.example.doriclingo.repositories

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.compose.runtime.rememberCoroutineScope
import com.example.doriclingo.model.CourseProgress
import com.example.doriclingo.room.entity.PendingProgress
import com.example.doriclingo.screens.DatabaseProvider
import com.example.doriclingo.util.isNetworkAvailable
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ProgressRepository(context: Context) {
    //gets the database instance
    private val db = DatabaseProvider.getDatabase(context)
    //gets the ID of the current user
    val userId = FirebaseAuth.getInstance().currentUser

    //fetches the user's progress
    fun getUserProgress(id: String): Flow<List<CourseProgress>> {
        return db.progressDao.getProgress(id).map { entity ->
            entity?.let {
                listOf(
                    CourseProgress("Conversation", it.conversation, it.lastConversation)
                )
            } ?: emptyList()
        }
    }

    //stores the user's progress in room database to be pushed to firebase
    suspend fun storePendingProgress(userId: String, progress: Float, lastSentence: Int) {
        //stores progress
        val pendingProgress = PendingProgress(
            id = userId,
            progress = progress,
            lastSentence = lastSentence
        )
        //inserts pending progress into room db
        db.pendingProgressDao.insert(
            pendingProgress
        )
    }

    //gets the user's pending progress
    suspend fun getPendingProgress(userId: String): List<PendingProgress> {
        return db.pendingProgressDao.getPendingProgress(userId)
    }

    //removes the user's pending progress when it is synced to firebase
    suspend fun deletePendingProgress(userId: String) {
        db.pendingProgressDao.deletePendingProgress(userId)
    }



    //pushes user progress to firebase
    suspend fun pushProgress(userId: String, progress: Float, lastSentence: Int, onResult: (Boolean, String?) -> Unit) {
        try {
            //sets the progress
            val progressData = mapOf(
                "conversation" to progress,
                "last_conversation" to lastSentence
            )
            //gets firebase firestore instance
            val db = Firebase.firestore
            //sets the target collection
            db.collection("progress")
                .document(userId)
                .update(progressData)
                .addOnSuccessListener {
                    onResult(true, null)
                }
                //returns error message if unsuccessful
                .addOnFailureListener { e ->
                    onResult(false, e.message)
                }
                //await is used to suspend the coroutine until the task is completed
                //this prevents issues such as race conditions or deadlocks
                .await()
        } catch (e: Exception) {
            onResult(false, e.message)
        }
    }



    //resets a user's progress
    //suspend function because the database operation is asynchronous
    suspend fun resetProgress(id: String){
        //resets the progress by setting the progress data to 0
        db.progressDao.updateProgress( 0f, 0, id)
    }


}
