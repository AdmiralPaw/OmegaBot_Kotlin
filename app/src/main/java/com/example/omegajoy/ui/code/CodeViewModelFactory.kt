package com.example.omegajoy.ui.code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.omegajoy.data.database.AppRoomDatabase

class CodeViewModelFactory(private val database: AppRoomDatabase) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CodeViewModel::class.java)) {
            return CodeViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}