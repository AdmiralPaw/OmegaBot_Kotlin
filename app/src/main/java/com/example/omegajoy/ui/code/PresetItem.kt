package com.example.omegajoy.ui.code

import com.example.omegajoy.data.dao.UserData
import com.example.omegajoy.data.entities.Command

data class PresetItem(
    val id: Int,
    val command: Command,
    val data: List<UserData>,
    var position: Int
)
