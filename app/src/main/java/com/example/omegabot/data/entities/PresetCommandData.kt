package com.example.omegabot.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = PresetCommand::class,
            parentColumns = ["id"],
            childColumns = ["presetCommandId"]
        ),
        ForeignKey(
            entity = CommandData::class,
            parentColumns = ["id"],
            childColumns = ["commandDataId"]
        )
    ]
)
data class PresetCommandData(
    // TODO: а что если ID закончатся?
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var presetCommandId: Int,
    var commandDataId: Int,
    var data: String
)
