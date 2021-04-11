package com.example.omegabot.data

import android.util.Log
import com.example.omegabot.data.model.LoggedInUser
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    private lateinit var client: OkHttpClient
    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

    fun login(username: String, password: String, login_mode: Boolean): Result<LoggedInUser> {
        try {
            // TODO: тут ли объявление клиента?
            Log.i("LoginDataSource", "client = OkHttpClient()")
            client = OkHttpClient()
            val json = when (login_mode) {
                false -> "{\"username\":\"$username\"," +
                        "\"password\":\"$password\"," +
                        "\"email\":\"$username@gmail.com\"}"
                true -> "{\"username\":\"$username\"," +
                        "\"password\":\"$password\"," +
                        "\"fingerprint\":\"$username@gmail.com\"," +
                        "\"User-Agent\":\"androidApp\"}"
            }

            val response = post("http://37.77.104.201:3000/auth", json, login_mode)
            if (response.code != 200) {
                throw Throwable("code is not 200")
            }
            // TODO: handle loggedInUser authentication
            val user = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
            return Result.Success(user)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }

    fun post(url: String, json: String, login_mode: Boolean): Response {
        val body = json.toRequestBody(JSON)
        val full_url = when (login_mode) {
            true -> url.plus("/login")
            false -> url.plus("/register")
        }
        val request = Request.Builder()
            .url(full_url)
            .post(body)
            .build()
        return client.newCall(request).execute()
    }
}