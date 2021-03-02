package com.example.omegajoy.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.omegajoy.data.dao.*
import com.example.omegajoy.data.entities.*

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = [User::class, Category::class, Command::class, Preset::class, CommandAndPreset::class],
    version = 1,
    exportSchema = false
)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun commandDao(): CommandDao
    abstract fun presetDao(): PresetDao
    abstract fun commandAndPresetDao(): CommandAndPresetDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getDatabase(context: Context): AppRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
                    "database"
                ).createFromAsset("database.db").build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

