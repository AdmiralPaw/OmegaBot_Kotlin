package com.example.omegajoy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.omegajoy.MainActivity
import com.example.omegajoy.R
import com.example.omegajoy.ui.FullFrameFragment
import com.example.omegajoy.ui.code.PresetItem
import com.jmedeisis.bugstick.Joystick
import com.jmedeisis.bugstick.JoystickListener
import okhttp3.WebSocket
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class HomeFragment : FullFrameFragment() {
    private var webSocket: WebSocket? = null
    private var presets: List<PresetList> = listOf()
    val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((activity as MainActivity).database)
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
            }

            override fun onDrag(degrees: Float, offset: Float) {
                val data0 = ArrayList<Byte>()
                data0.add(0, 0xFF.toByte())
                data0.add(1, 0xFE.toByte())
                data0.add(2, (100 and 0xFF).toByte())


                //ArrayList обусловлен методом add для добавления данных
                // в конец массива, ибо изначально кол-во байт данных не известно
                for (dataByte in convertJoystickToDrive(
                    angleConvert(degrees),
                    distanceConvert(offset)
                )) {
                    data0.add(dataByte)
                }

                data0.add(0xFF.toByte())
                send(data0)
            }

            override fun onUp() {
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

    fun send(command: ArrayList<Byte>) {
        val commandStr = StringBuilder()
        for (data_byte: Byte in command) {
            commandStr.append(String.format("%02x", data_byte).toUpperCase())
        }
        webSocket?.send(
            "{\"type\":\"cmd\"," +
                    "\"body\":\"${commandStr}\"}"
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

    /**
     * @author AdmPaw
     * @param angel угол от 0 до 360
     * @param offset отклонение от 0 до 100
     * @return два байта данных скорости и направления движения двигателей (гусениц) робота
     * в соответствии с ПРОТОКОЛОМ
     */
    fun convertJoystickToDrive(angel: Int, offset: Int): ByteArray {
        val data = ByteArray(2)
        //TODO добавить ссылку на объяснение типа данного движения
        val x = floor(offset * cos(Math.toRadians(angel.toDouble())))
            .toInt()
        val y = floor(offset * sin(Math.toRadians(angel.toDouble())))
            .toInt()
        var leftEngine: Int
        var rightEngine: Int
        var left = 1
        var right = 1
        val rotateSpeed = abs(x)
        when {
            x > 0 -> {
                leftEngine = y + rotateSpeed
                rightEngine = y - rotateSpeed
            }
            x < 0 -> {
                leftEngine = y - rotateSpeed
                rightEngine = y + rotateSpeed
            }
            else -> {
                leftEngine = y
                rightEngine = y
            }
        }
        //ПРОТОКОЛ диктует скорость от -100 до 100, где впоследствии преобразуется
        // в промежуток от 0 до 100 со значением направления движения
        if (leftEngine > 100) leftEngine = 100
        if (leftEngine < -100) leftEngine = -100
        if (rightEngine > 100) rightEngine = 100
        if (rightEngine < -100) rightEngine = -100
        if (leftEngine < 0) {
            leftEngine = abs(leftEngine)
            left = 0
        }
        if (rightEngine < 0) {
            rightEngine = abs(rightEngine)
            right = 0
        }
        //ПРОТОКОЛ диктует ставить направление движения (вперед - 1, назад - 0)
        // в старший бит байта, остальные 7-мь бит заполнять значением скорости от 0 до 100
        data[0] = (left shl 7 or (leftEngine and 0xFF)).toByte()
        data[1] = (right shl 7 or (rightEngine and 0xFF)).toByte()
        return data
    }
}

data class PresetList(
    val currentList: List<PresetItem>,
    val attachedButton: String
)
