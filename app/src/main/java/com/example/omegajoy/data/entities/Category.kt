package com.example.omegajoy.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey var id: Int,
    var name: String?
)