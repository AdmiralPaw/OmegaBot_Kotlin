package com.example.omegajoy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.omegajoy.data.entities.PresetCommand

@Dao
interface PresetCommandDao {
    @Insert
    suspend fun insert(PresetCommand: PresetCommand): Long

    @Update
    suspend fun update(PresetCommand: PresetCommand)

    @Query("DELETE FROM PresetCommand WHERE PresetCommand.presetId IN (SELECT id FROM Preset WHERE Preset.attachedButton = :name) AND PresetCommand.position = :position")
    suspend fun deleteByPosition(name: String, position: Int)

    @Query("SELECT PresetCommand.id, PresetCommand.commandId, PresetCommand.presetId, PresetCommand.position FROM PresetCommand JOIN Preset ON presetId = Preset.id WHERE Preset.attachedButton = :name")
    suspend fun getAllByButtonName(name: String): List<PresetCommand>

    @Query("SELECT MAX(PresetCommand.position) FROM PresetCommand JOIN Preset ON presetId = Preset.id WHERE Preset.attachedButton = :name")
    suspend fun getLatestPositionByButtonName(name: String): List<Int>

    @Query("SELECT PresetCommand.position FROM PresetCommand WHERE PresetCommand.id = :id")
    suspend fun getPositionById(id: Int): Int

    @Query("DELETE FROM PresetCommand WHERE PresetCommand.id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT PresetCommand.presetId FROM PresetCommand WHERE PresetCommand.id = :id")
    suspend fun getPresetIdById(id: Int): Int
}