package com.example.omegajoy.ui.code

import com.example.omegajoy.data.entities.Command
import com.example.omegajoy.data.entities.Data

data class PresetItem(
    val id: Int,
    val command: Command,
    val data: List<Data>,
    val position: Int,
    var onScreen: Boolean = false
)
