package com.example.omegajoy.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "command_and_preset",
    foreignKeys = [
        ForeignKey(
            entity = Command::class,
            parentColumns = ["id"],
            childColumns = ["command_id"]
        ),
        ForeignKey(
            entity = Preset::class,
            parentColumns = ["id"],
            childColumns = ["preset_id"]
        )
    ]
)
data class CommandAndPreset(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var command_id: Int,
    var preset_id: Int,
    var position: Int
)