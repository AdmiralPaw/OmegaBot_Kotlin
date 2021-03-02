package com.example.omegajoy.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.omegajoy.data.entities.Command
import com.example.omegajoy.data.entities.Preset

@Dao
interface PresetDao {
    @Query(
        "SELECT command.id, command.name, command.category_id FROM preset " +
                "JOIN command_and_preset ON preset.id = command_and_preset.preset_id " +
                "JOIN command ON command.id = command_and_preset.command_id " +
                "WHERE preset.attachedButton = :name"
    )
    suspend fun getCommandsByButtonName(name: String): List<Command>

    @Query("SELECT * FROM preset  WHERE preset.attachedButton = :name")
    suspend fun getPresetByButtonName(name: String): Preset
}