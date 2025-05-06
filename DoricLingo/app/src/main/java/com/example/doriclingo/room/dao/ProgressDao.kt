package com.example.doriclingo.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.doriclingo.room.entity.ProgressEntity

//talk about how DAO handles SQL behind the scenes - prepared statements are not necessary because room makes it a prepared statement behind the scene using the DAO
@Dao
interface ProgressDao {

    //upsert could potentially overwrite someone's data if they are already found in the database
    //insert would not overwrite existing data with the on conflict strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProgress(progress: ProgressEntity)

    //updates the user progress
    @Query("UPDATE UserProgress SET conversation = :conversation, lastConversation = :lastConversation WHERE id = :id")
    suspend fun updateProgress(conversation: Float, lastConversation: Int, id: String)

    //used to delete user progress from the database
    @Query("DELETE FROM UserProgress WHERE id = :id")
    suspend fun deleteProgress(id: String)

    //used to get user progress for the graph
    @Query("SELECT * FROM UserProgress WHERE id = :id")
    fun getProgress(id: String): Flow<ProgressEntity>

    //used to get the last completed conversation phrase to save the user's progress
    @Query("SELECT lastConversation FROM UserProgress WHERE id = :id")
    fun getConversation(id: String): Flow<Int>


}