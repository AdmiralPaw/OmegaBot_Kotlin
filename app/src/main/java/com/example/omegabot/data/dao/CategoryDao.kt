package com.example.omegabot.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.omegabot.data.entities.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category")
    suspend fun getAll(): List<Category>
}