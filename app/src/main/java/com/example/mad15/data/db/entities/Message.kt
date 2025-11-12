package com.example.mad15.data.db.entities

data class Message(
    val username: String = "",   // default values needed for Firestore deserialization
    val message: String = "",
    val date: String = ""
)