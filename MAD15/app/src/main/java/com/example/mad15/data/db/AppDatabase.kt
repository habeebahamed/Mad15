package com.example.mad15.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mad15.data.db.dao.UserDao
import com.example.mad15.data.db.entities.User

//Defines the Room database and links DAOs + entities
@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}