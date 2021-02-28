package com.example.omegajoy.data.database

import android.app.Application
import com.example.omegajoy.data.LoginRepository

class AppDatabaseApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { AppRoomDatabase.getDatabase(this) }
    val loginRepository by lazy { LoginRepository(database.userDao()) }
    val userRepository by lazy { database.userDao() }
    val categoryRepository by lazy { database.categoryDao() }
}