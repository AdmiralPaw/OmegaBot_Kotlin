package com.example.omegajoy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.omegajoy.data.entities.Command
import com.example.omegajoy.data.entities.Preset

@Dao
interface PresetDao {
    @Query(
        "SELECT Command.id, Command.name, Command.categoryId " +
                "FROM Preset JOIN PresetCommand ON Preset.id = PresetCommand.presetId " +
                "JOIN Command ON Command.id = PresetCommand.commandId " +
                "WHERE Preset.attachedButton = :name"
    )
    suspend fun getCommandsByButtonName(name: String): List<Command>

    @Query("SELECT * FROM Preset  WHERE Preset.attachedButton = :name")
    suspend fun getPresetByButtonName(name: String): Preset

    @Insert
    suspend fun insert(preset: Preset)
}