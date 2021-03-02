package com.example.omegajoy.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.omegajoy.data.entities.Command

@Dao
interface CommandDao {
    @Query("SELECT * FROM command")
    suspend fun getAll(): List<Command>

    @Query("SELECT * FROM command WHERE category_id = :id")
    suspend fun getByCategoryId(id: Int): List<Command>

    @Query("SELECT command.id, command.name, command.category_id FROM command JOIN category ON command.category_id = category.id WHERE category.name = :name")
    suspend fun getByCategoryName(name: String): List<Command>

    @Query("SELECT * FROM command WHERE name = :name")
    suspend fun getByName(name: String): Command
}