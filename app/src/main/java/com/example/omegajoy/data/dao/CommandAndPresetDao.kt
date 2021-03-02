package com.example.omegajoy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.omegajoy.data.entities.CommandAndPreset

@Dao
interface CommandAndPresetDao {
    @Insert
    suspend fun insert(commandAndPreset: CommandAndPreset)

    @Update
    suspend fun update(commandAndPreset: CommandAndPreset)

    @Query("DELETE FROM command_and_preset WHERE preset_id IN (SELECT id FROM preset WHERE preset.attachedButton = :name) AND position = :position")
    suspend fun deleteByPosition(name: String, position: Int)

    @Query("SELECT command_and_preset.id, command_and_preset.preset_id, command_and_preset.command_id, command_and_preset.position FROM command_and_preset JOIN preset ON preset_id = preset.id WHERE preset.attachedButton = :name")
    suspend fun getAllByButtonName(name: String): List<CommandAndPreset>

    @Query("SELECT MAX(command_and_preset.position) FROM command_and_preset JOIN preset ON preset_id = preset.id WHERE preset.attachedButton = :name")
    suspend fun getLatestPositionByButtonName(name: String): List<Int>
}