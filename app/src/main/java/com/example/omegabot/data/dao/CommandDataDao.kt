package com.example.omegabot.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.omegabot.data.entities.CommandData
import com.example.omegabot.data.entities.Data

@Dao
interface CommandDataDao {
    @Query("SELECT Data.id, Data.name, Data.type, Data.valueMin, Data.valueMax, Data.defaultData FROM CommandData JOIN Data ON Data.id = CommandData.dataId WHERE CommandData.commandId = :id")
    suspend fun getDataByCommandId(id: Int): List<Data>

    @Query("SELECT * FROM CommandData WHERE CommandData.commandId = :id")
    suspend fun getByCommandId(id: Int): List<CommandData>

    @Query("SELECT * FROM CommandData WHERE id = :id")
    suspend fun getById(id: Int): CommandData
}