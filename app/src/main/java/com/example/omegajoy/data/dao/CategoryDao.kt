package com.example.omegajoy.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.omegajoy.data.entities.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    suspend fun getAll(): List<Category>
}