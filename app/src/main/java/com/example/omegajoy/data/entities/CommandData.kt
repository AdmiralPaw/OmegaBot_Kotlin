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
            entity = Data::class,
            parentColumns = ["id"],
            childColumns = ["dataId"]
        )
    ]
)
data class CommandData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var commandId: Int,
    var dataId: Int,
    var position: Int
)
