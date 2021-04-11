package com.example.omegabot.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.omegabot.data.entities.Robot

@Dao
interface RobotDao {
    @Query("SELECT * FROM Robot")
    suspend fun getAll(): Robot

    @Query("SELECT * FROM Robot WHERE connectionType = :connectionType")
    suspend fun getAllByConnectionType(connectionType: String): List<Robot>

    @Insert
    suspend fun insert(robot: Robot)
}