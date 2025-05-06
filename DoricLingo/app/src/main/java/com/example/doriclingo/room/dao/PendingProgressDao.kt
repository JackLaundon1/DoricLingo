package com.example.doriclingo.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.doriclingo.room.entity.PendingProgress

//data access object
@Dao
interface PendingProgressDao {
    //inserts the progress
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: PendingProgress)

    //fetches the pending progress to be pushed
    @Query("SELECT * FROM PendingProgress WHERE id = :userId")
    suspend fun getPendingProgress(userId: String): List<PendingProgress>

    //deletes the pending progress when it is synced to firebase
    @Query("DELETE FROM PendingProgress WHERE id = :id")
    suspend fun deletePendingProgress(id: String)
}
