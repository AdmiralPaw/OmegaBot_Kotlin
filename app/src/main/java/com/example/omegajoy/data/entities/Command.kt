package com.example.omegajoy.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "command",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["category_id"]
    )]
)
data class Command(
    @PrimaryKey var id: Int,
    var name: String?,
    var category_id: Int?
)