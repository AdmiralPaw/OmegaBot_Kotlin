package com.example.omegabot.ui.robotmanager.add_robot

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.omegabot.data.database.AppRoomDatabase

class AddRobotModelFactory(
    private val database: AppRoomDatabase,
    private val fragment: Fragment
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddRobotViewModel::class.java)) {
            return AddRobotViewModel(database, fragment) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}