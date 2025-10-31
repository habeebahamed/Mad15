package com.example.mad15.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

//creates a users table with id, username, email, and password
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String
)