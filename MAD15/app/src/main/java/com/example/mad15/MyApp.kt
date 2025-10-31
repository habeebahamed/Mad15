package com.example.mad15

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.example.mad15.data.db.AppDatabase

class MyApp : Application() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize Room database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "myapp_database"
        )
            .fallbackToDestructiveMigration() // Drop and recreate DB on version change
            .build()
    }
}