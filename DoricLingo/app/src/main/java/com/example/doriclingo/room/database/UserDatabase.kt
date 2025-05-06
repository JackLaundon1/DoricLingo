package com.example.doriclingo.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.doriclingo.room.dao.PendingProgressDao
import com.example.doriclingo.room.dao.ProgressDao
import com.example.doriclingo.room.dao.UserDao
import com.example.doriclingo.room.entity.PendingProgress
import com.example.doriclingo.room.entity.UserEntity
import com.example.doriclingo.room.entity.ProgressEntity

//defines the database
@Database(
    entities = [UserEntity::class, ProgressEntity::class, PendingProgress::class],
    //current database version
    version = 5
)
abstract class UserDatabase : RoomDatabase() {
    //accessors to DAOs
    //accessor for user entity
    abstract val dao: UserDao
    //accessor for progress entity
    abstract val progressDao: ProgressDao
    //accessor for pending progress entity
    abstract val pendingProgressDao: PendingProgressDao
}
