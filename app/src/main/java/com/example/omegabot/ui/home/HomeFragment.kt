package com.example.omegabot.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.omegabot.MainActivity
import com.example.omegabot.R
import com.example.omegabot.ui.FullFrameFragment
import com.example.omegabot.ui.code.PresetItem
import com.jmedeisis.bugstick.Joystick
import com.jmedeisis.bugstick.JoystickListener
import okhttp3.WebSocket

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class HomeFragment : FullFrameFragment() {
    private var webSocket: WebSocket? = null
    private var presets: List<PresetList> = listOf()
    val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((activity as MainActivity).database, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val joystickLeft = root.findViewById<Joystick>(R.id.joystick_left)

        val buttonToCode: ImageButton = root.findViewById(R.id.button_to_code)
        buttonToCode.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_code)
        )
        val buttonToMenu: ImageButton = root.findViewById(R.id.button_to_menu)
        buttonToMenu.setOnClickListener {
            (activity as MainActivity).openDrawer()
        }
        val leftButton: ImageButton = root.findViewById(R.id.left_button)
        val rightButton: ImageButton = root.findViewById(R.id.right_button)
        val topButton: ImageButton = root.findViewById(R.id.top_button)
        val bottomButton: ImageButton = root.findViewById(R.id.bottom_button)

        leftButton.setOnClickListener { onClickPresetButton(it) }
        rightButton.setOnClickListener { onClickPresetButton(it) }
        topButton.setOnClickListener { onClickPresetButton(it) }
        bottomButton.setOnClickListener { onClickPresetButton(it) }

        joystickLeft.setJoystickListener(object : JoystickListener {
            override fun onDown() {
                homeViewModel.sendStoppers()
                homeViewModel.toggle = true
                homeViewModel.sendData()
            }

            override fun onDrag(degrees: Float, offset: Float) {
                homeViewModel.lastAngle = angleConvert(degrees)
                homeViewModel.lastDistance = distanceConvert(offset)
            }

            override fun onUp() {
                homeViewModel.sendStoppers()
                homeViewModel.toggle = false
            }
        })

        // TODO: получение от CodeFragment.kt листов пресетов
        homeViewModel.preLoad(listOf(leftButton, rightButton, topButton, bottomButton))

        homeViewModel.presetList.observe(viewLifecycleOwner, Observer {
            val presetList = it ?: return@Observer

            presets = presetList
        })


        webSocket = (activity as MainActivity).ws

        return root
    }

    fun send(data: MutableList<Int>) {
        val mydata = "\"${data[0]}\",\"${data[1]}\""
        val commandStr = "{\"id\":\"10\",\"data\":[${mydata}]}"
        webSocket?.send(
            "{\"type\":\"cmd\"," +
                    "\"body\":${commandStr}}"
        )
    }

    fun sendPreset(presetList: PresetList) {
        val commandsJSON = presetList.currentList.map { it.toJSON() }
        for (command in commandsJSON) {
            webSocket?.send(
                "{\"type\":\"cmd\"," +
                        "\"body\":${command}}"
            )
            println(
                "{\"type\":\"cmd\"," +
                        "\"body\":${command}}"
            )
        }
    }

    fun onClickPresetButton(view: View) {
        val preset = presets.find {
            it.attachedButton == view.contentDescription
        }
        if (preset == null) {
            Toast.makeText(
                activity,
                "Нет пресета на ${view.contentDescription}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            sendPreset(preset)
        }
    }

    fun angleConvert(degrees: Float): Int {
        return if (degrees < 0)
            360 + degrees.toInt()
        else
            degrees.toInt()
    }

    fun distanceConvert(offset: Float): Int {
        return (offset * 100).toInt()
    }

}

data class PresetList(
    val currentList: List<PresetItem>,
    val attachedButton: String
)
