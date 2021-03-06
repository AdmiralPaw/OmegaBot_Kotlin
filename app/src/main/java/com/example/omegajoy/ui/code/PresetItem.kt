package com.example.omegajoy.ui.code

import com.example.omegajoy.data.dao.UserData
import com.example.omegajoy.data.entities.Command

data class PresetItem(
    val id: Int,
    val command: Command,
    val data: List<UserData>,
    var position: Int
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
}