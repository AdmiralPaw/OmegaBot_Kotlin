package com.example.omegajoy.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey var id: String,
    var access_token: String?,
    var refresh_token: String?,
    var username: String?,
    var latest: Int?
)