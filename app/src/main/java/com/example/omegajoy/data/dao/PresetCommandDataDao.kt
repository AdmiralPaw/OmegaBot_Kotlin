package com.example.omegajoy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.omegajoy.data.entities.PresetCommandData
import com.example.omegajoy.ui.code.UserData

@Dao
interface PresetCommandDataDao {
    @Insert
    suspend fun insert(presetCommandData: PresetCommandData)

    @Query("SELECT PresetCommandData.id, Data.name, Data.type, Data.valueMin, Data.valueMax, PresetCommandData.data, CommandData.position FROM PresetCommandData JOIN CommandData ON PresetCommandData.commandDataId = CommandData.id JOIN Data ON Data.id = CommandData.dataId JOIN PresetCommand ON PresetCommandData.presetCommandId = PresetCommand.id WHERE CommandData.commandId = :id AND PresetCommand.position = :position")
    suspend fun getDataByCommandId(id: Int, position: Int): List<UserData>

    @Query("SELECT * FROM PresetCommandData JOIN CommandData ON commandDataId = commandData.id WHERE commandId = :id")
    suspend fun getByCommandId(id: Int): List<PresetCommandData>

    @Query("UPDATE PresetCommandData SET data = :data WHERE id = :id")
    fun updateDataById(id: Int, data: String)

    @Query("SELECT * FROM PresetCommandData WHERE presetCommandId = :id")
    suspend fun getByPresetCommandId(id: Int): List<PresetCommandData>

    @Query("DELETE FROM PresetCommandData WHERE presetCommandId = :id")
    suspend fun deleteByPresetCommandId(id: Int)
}