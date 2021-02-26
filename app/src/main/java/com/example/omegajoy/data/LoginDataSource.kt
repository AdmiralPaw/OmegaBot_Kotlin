package com.example.omegajoy.data

import android.util.Log
import com.example.omegajoy.data.model.LoggedInUser
import okhttp3.OkHttpClient
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: тут ли объявление клиента?
            Log.i("LoginDataSource", "client = OkHttpClient()")
            val client = OkHttpClient()

            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}