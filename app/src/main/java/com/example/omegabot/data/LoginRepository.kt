package com.example.omegabot.data

import android.util.Log
import com.example.omegabot.data.dao.UserDao
import com.example.omegabot.data.entities.User
import com.example.omegabot.data.model.LoggedInUser
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val userDao: UserDao) {

    private lateinit var client: OkHttpClient
    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set
//
//    val isLoggedIn: Boolean
//        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }
//
//    fun logout() {
//        user = null
//        dataSource.logout()
//    }

    fun preLogin(): Result<LoggedInUser> {
        val result = doRefresh()
        return result
    }

    fun login(username: String, password: String, login_mode: Boolean): Result<LoggedInUser> {
        // handle login
        val result = doPost(username, password, login_mode)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun doPost(
        username: String,
        password: String,
        login_mode: Boolean
    ): Result<LoggedInUser> {
        // TODO: тут ли объявление клиента?
        Log.i("LoginDataSource", "client = OkHttpClient()")
        client = OkHttpClient()
        val json_register = "{\"username\":\"$username\"," +
                "\"password\":\"$password\"}"
        val json_login = "{\"username\":\"$username\"," +
                "\"password\":\"$password\"," +
                "\"fingerprint\":\"$username@gmail.com\"}"
        val json = when (login_mode) {
            true -> json_login
            false -> json_register
        }
        var response = post("http://37.77.104.201:3000/auth", json, login_mode)
        if (response.code != 200) {
            return Result.Error(Exception("LoginException"))
        }
        if (!login_mode) {
            response = post("http://37.77.104.201:3000/auth", json_login, true)
            if (response.code != 200) {
                return Result.Error(Exception("LoginException"))
            }
        }
        val user = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
        val answer = JSONObject(response.body!!.string())
        userDao.insert(
            User(
                user.userId,
                answer.getJSONObject("tokens").getString("accessToken"),
                answer.getJSONObject("tokens").getString("refreshToken"),
                user.displayName, 1
            )
        )
        userDao.setCurrentUserToLatest(user.userId)
        return Result.Success(user)
    }

    private fun doRefresh(): Result<LoggedInUser> {
        // TODO: тут ли объявление клиента?
        Log.i("LoginDataSource", "client = OkHttpClient()")
        client = OkHttpClient()
        val latestUser = userDao.getLatestUser()
        // TODO: оформить проверку на наличие юзеров вообще
        if (latestUser == null) {
            return Result.Error(Exception("AutoLoginException"))
        }
        val json = "{\"refreshToken\":\"${latestUser.refresh_token}\"," +
                "\"fingerprint\":\"${latestUser.username}@gmail.com\"}"
        val header = latestUser.access_token!!
        val response = postRefresh("http://37.77.104.201:3000/auth", json, header)
        if (response.code != 200) {
            return Result.Error(Exception("AutoLoginException"))
        }
        // TODO: handle loggedInUser authentication
        val user = LoggedInUser(
            latestUser.id,
            latestUser.username.toString()
        )
        val answer = JSONObject(response.body!!.string())
        latestUser.access_token = answer.getJSONObject("tokens").getString("accessToken")
        latestUser.refresh_token = answer.getJSONObject("tokens").getString("refreshToken")
        userDao.updateUser(latestUser)
        userDao.setCurrentUserToLatest(user.userId)
        return Result.Success(user)
    }


    private fun post(url: String, json: String, login_mode: Boolean): Response {
        val body = json.toRequestBody(JSON)
        val full_url = when (login_mode) {
            true -> url.plus("/login")
            false -> url.plus("/register")
        }
        var request_builder = Request.Builder()
            .url(full_url)
            .post(body)
        if (login_mode) {
            request_builder = request_builder.addHeader("User-Agent", "androidApp")
        }
        val request = request_builder.build()
        return client.newCall(request).execute()
    }

    private fun postRefresh(url: String, json: String, header: String): Response {
        val body = json.toRequestBody(JSON)
        val full_url = url.plus("/refresh")
        val request = Request.Builder()
            .url(full_url)
            .post(body)
            .addHeader("authorization", "Bearer $header")
            .addHeader("User-Agent", "androidApp")
            .build()
        return client.newCall(request).execute()
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}