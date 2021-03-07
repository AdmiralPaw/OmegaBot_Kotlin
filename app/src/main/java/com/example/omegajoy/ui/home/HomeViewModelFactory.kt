package com.example.omegajoy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.omegajoy.data.database.AppRoomDatabase
import com.example.omegajoy.ui.code.CodeViewModel

class HomeViewModelFactory(private val database: AppRoomDatabase) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}