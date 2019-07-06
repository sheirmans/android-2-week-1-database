package com.ucsdextandroid2.todoroom

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Created by rjaylward on 2019-07-05
 */
abstract class AppDatabase {

    companion object {

        private const val DB_NAME = "notesapp.db"

        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context): AppDatabase = TODO()
    }

}
