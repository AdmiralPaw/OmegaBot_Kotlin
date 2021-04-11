package com.example.omegabot.ui.robotmanager.add_robot

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omegabot.data.database.AppRoomDatabase
import com.example.omegabot.data.entities.Robot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class AddRobotViewModel(dataSource: AppRoomDatabase, val fragment: Fragment) : ViewModel() {
    private var userDao = dataSource.userDao()
    private var robotDao = dataSource.robotDao()
    private lateinit var client: OkHttpClient

    private val _robotsInternet = MutableLiveData<List<Robot>>()
    val robotsInternet: LiveData<List<Robot>> = _robotsInternet

    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

    fun addRobot(str: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                client = OkHttpClient()
                val json = "{\"robotUUID\":\"${str}\"}"
                val body = json.toRequestBody(JSON)
                val full_url = "http://37.77.104.201:3000/robot/robot"
                var request_builder = Request.Builder()
                    .url(full_url)
                    .post(body)

                val accessToken = userDao.getLatestUser().access_token
                request_builder = request_builder.addHeader("authorization", "Bearer $accessToken")

                val request = request_builder.build()

                val response = client.newCall(request).execute()
                if (response.code == 200) {
                    try {
                        val responseJSON = JSONObject(response.body!!.string())
                        val uuid = responseJSON.getJSONObject("message").getInt("robotId")
                        robotDao.insert(Robot(name = str, uuid = str))
                    } catch (error: Exception) {
                        Log.e("[addRobot]", error.toString())
                    }
                    (fragment as AddRobotFragment).backToManager()
                }
            } catch (error: Exception) {
                Log.e("[addRobot]", error.toString())
            }
        }
    }

    fun getRobotsByConnectionType(connectionType: String) {
        if (connectionType == "internet" || connectionType == "bluetooth") {
            viewModelScope.launch(Dispatchers.IO) {
                _robotsInternet.postValue(robotDao.getAllByConnectionType(connectionType))
            }
        }
    }
}