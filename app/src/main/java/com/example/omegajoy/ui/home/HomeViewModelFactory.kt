package com.example.omegajoy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.omegajoy.data.database.AppRoomDatabase

class HomeViewModelFactory(
    private val database: AppRoomDatabase,
    private val homeFragment: HomeFragment
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(database, homeFragment) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}