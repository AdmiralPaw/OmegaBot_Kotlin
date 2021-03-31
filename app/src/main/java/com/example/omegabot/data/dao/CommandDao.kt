package com.example.omegabot.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.omegabot.data.entities.Command

@Dao
interface CommandDao {
    @Query("SELECT * FROM Command")
    suspend fun getAll(): List<Command>

    @Query("SELECT * FROM Command WHERE categoryId = :id")
    suspend fun getByCategoryId(id: Int): List<Command>

    @Query("SELECT Command.id, Command.name, Command.categoryId, Command.robotId FROM Command JOIN Category ON Category.id = Command.categoryId WHERE Category.name = :name")
    suspend fun getByCategoryName(name: String): List<Command>

    @Query("SELECT * FROM Command WHERE name = :name")
    suspend fun getByName(name: String): Command

    @Query("SELECT * FROM Command WHERE id = :id")
    suspend fun getById(id: Int): Command
}