package com.example.omegajoy.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preset")
data class Preset(
    @PrimaryKey var id: Int,
    var name: String?,
    var attachedButton: String?
)