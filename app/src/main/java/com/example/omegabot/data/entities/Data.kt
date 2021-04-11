package com.example.omegabot.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Data(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val valueMin: Int?,
    val valueMax: Int?,
    val defaultData: String
)