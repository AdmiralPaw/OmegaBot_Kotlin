package com.example.omegajoy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.omegajoy.data.dao.CategoryDao
import com.example.omegajoy.data.dao.UserDao
import com.example.omegajoy.data.database.AppDatabaseApplication
import com.example.omegajoy.data.database.AppRoomDatabase
import com.google.android.material.navigation.NavigationView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okio.ByteString
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var client: OkHttpClient
    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    private var listener: EchoWebSocketListener? = null
    private var ws: WebSocket? = null
    private lateinit var userDao: UserDao
    lateinit var categoryDao: CategoryDao
    lateinit var database: AppRoomDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_robot_manager,
                R.id.nav_notifications,
                R.id.nav_settings
            ), drawerLayout
        )
        navView.setupWithNavController(navController)

        userDao = (application as AppDatabaseApplication).userRepository
        database = (application as AppDatabaseApplication).database
        categoryDao = (application as AppDatabaseApplication).categoryRepository

        client = OkHttpClient()
        listener = EchoWebSocketListener()
        ws = client.newWebSocket(
            Request.Builder().url("ws://37.77.104.201:1337").build(),
            listener!!
        )
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun openDrawer() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.openDrawer(GravityCompat.START)
    }

    fun send(command: ArrayList<Byte>) {
        val command_str = StringBuilder()
        for (data_byte: Byte in command) {
            command_str.append(String.format("%02x", data_byte).toUpperCase())
        }
        ws?.send(
            "{\"type\":\"cmd\"," +
                    "\"body\":\"" + command_str + "\"}"
        )
    }

    fun changeRobot(id: String) {
        val str = "{\"type\":\"changeRobot\"," +
                "\"body\":\"${id}\"}"
        ws?.send(str)
    }

    inner class EchoWebSocketListener : WebSocketListener() {
        private val NORMAL_CLOSURE_STATUS = 1000

        override fun onOpen(webSocket: WebSocket, response: Response) {
            val str = ("{\"type\":\"authUser\"," +
                    "\"body\":\"${userDao.getLatestUser().access_token}\"}")
            webSocket.send(str)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            println("Receiving: $text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            println("Receiving: " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            println("Closing: $code $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            t.printStackTrace()
        }
    }

}
