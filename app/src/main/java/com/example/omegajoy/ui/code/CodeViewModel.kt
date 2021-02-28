package com.example.omegajoy.ui.code

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omegajoy.data.database.AppRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CodeViewModel(dataSource: AppRoomDatabase) : ViewModel() {
    val categoryDao = dataSource.categoryDao()
    private val _categoryList = MutableLiveData<CategoryList>()
    val categoryList: LiveData<CategoryList> = _categoryList

    fun preLoad() {
        viewModelScope.launch(Dispatchers.IO) {
            _categoryList.postValue(
                CategoryList(names = categoryDao.getAll().map { "${it.name}" })
            )
        }
    }
}