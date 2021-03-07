package com.example.omegajoy.data.dao

import androidx.room.*
import com.example.omegajoy.data.entities.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM user WHERE latest = 1")
    fun getLatestUser(): User

    @Query("UPDATE user SET access_token = :access_token WHERE id = :id")
    fun updateAccessTokenById(id: String, access_token: String)

    @Query("UPDATE user SET refresh_token = :refresh_token WHERE id = :id")
    fun updateRefreshTokenById(id: String, refresh_token: String)

    @Query("UPDATE user SET latest = 1 WHERE id = :id")
    fun _setCurrentUserToLatest(id: String)

    @Query("UPDATE user SET latest = 0 WHERE id != :id")
    fun _setAnotherUserToNotLatest(id: String)

    fun setCurrentUserToLatest(id: String) {
        _setCurrentUserToLatest(id)
        _setAnotherUserToNotLatest(id)
    }
}