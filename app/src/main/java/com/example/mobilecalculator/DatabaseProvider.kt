package com.example.mobilecalculator

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {

        println("geting database")

        return database ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "AppDatabase"
            ).build()
            database = instance
            instance
        }
    }
}
