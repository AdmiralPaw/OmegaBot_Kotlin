package com.example.omegajoy.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Command::class,
            parentColumns = ["id"],
            childColumns = ["commandId"]
        ),
        ForeignKey(
            entity = Preset::class,
            parentColumns = ["id"],
            childColumns = ["presetId"]
        )
    ]
)
data class PresetCommand(
    // TODO: а что если ID закончатся?
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var commandId: Int,
    var presetId: Int,
    var position: Int
)