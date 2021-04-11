package com.example.omegabot.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.omegabot.data.entities.Preset

@Dao
interface PresetDao {
    @Query(
        "SELECT Command.id, Command.name, Command.categoryId, PresetCommand.position " +
                "FROM Preset JOIN PresetCommand ON Preset.id = PresetCommand.presetId " +
                "JOIN Command ON Command.id = PresetCommand.commandId " +
                "WHERE Preset.attachedButton = :name"
    )
    suspend fun getCommandsByButtonName(name: String): List<CommandAndPosition>

    @Query("SELECT * FROM Preset  WHERE Preset.attachedButton = :name")
    suspend fun getPresetByButtonName(name: String): Preset

    @Insert
    suspend fun insert(preset: Preset)
}

data class CommandAndPosition(
    val id: Int = 0,
    val name: String,
    val categoryId: Int,
    val position: Int
)