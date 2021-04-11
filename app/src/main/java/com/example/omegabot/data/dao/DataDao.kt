package com.example.omegabot.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.omegabot.data.entities.Data

@Dao
interface DataDao {
    @Query("SELECT * FROM Data WHERE id = :dataId")
    suspend fun getById(dataId: Int): Data
}