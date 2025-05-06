package com.example.doriclingo.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.doriclingo.room.entity.UserEntity

//data access object
@Dao

interface UserDao {

    //the on conflict strategy ensures that user details will not be added to the database twice.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    //suspend allows coroutines to block until database operation is complete
    suspend fun insertUser(user: UserEntity)
    //deletes user from the room database
    @Query("DELETE FROM UserDetails WHERE id = :id")
    suspend fun deleteUser(id: String)
    //gets userID from the database
    @Query("SELECT *  FROM UserDetails WHERE id = :id")
    fun getUser(id: String?): Flow<List<UserEntity>>
}