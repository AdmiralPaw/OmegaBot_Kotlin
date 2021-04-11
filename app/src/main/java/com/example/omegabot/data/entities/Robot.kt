package com.example.omegabot.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["uuid"], unique = true)])
data class Robot(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String?,
    val uuid: String,
    val connectionType: String = "internet"
)
