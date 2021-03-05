package com.example.omegajoy.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.omegajoy.data.entities.Data

@Dao
interface CommandDataDao {
    @Query("SELECT Data.id, Data.name, Data.type, Data.valueMin, Data.valueMax, Data.defaultData FROM CommandData JOIN Data ON Data.id = CommandData.dataId WHERE CommandData.commandId = :id")
    fun getDataByCommandId(id: Int): List<Data>
}