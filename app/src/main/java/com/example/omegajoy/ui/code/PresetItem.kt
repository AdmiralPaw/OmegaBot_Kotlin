package com.example.omegajoy.ui.code

import com.example.omegajoy.data.entities.Command
import com.example.omegajoy.data.entities.PresetCommand

data class PresetItem(
    val id: Int,
    var position: Int,
    val command: Command,
    val data: List<UserData>
) {
    fun toJSON(): String {
        val header = "\"id\":\"${command.id}\""
        val bodyItems = data.map {
            "{\"name\":\"${it.name}\",\"value\":\"${it.data}\"}"
        }
        val body = when (bodyItems.isNullOrEmpty()) {
            true -> ""
            false -> bodyItems.reduce { total, next ->
                "$total,$next"
            }
        }
        return "{$header,\"data\":[$body]}"
    }

    fun toPresetCommandNoPresetId(): PresetCommand {
        return PresetCommand(
            id = id,
            commandId = command.id,
            presetId = -1,
            position = position
        )
    }
}

data class UserData(
    val id: Int,
    val name: String,
    val type: String,
    val valueMin: Int?,
    val valueMax: Int?,
    var data: String,
    val position: Int
)