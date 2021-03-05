package com.example.omegajoy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.Navigation
import com.example.omegajoy.MainActivity
import com.example.omegajoy.R
import com.example.omegajoy.ui.FullFrameFragment
import com.jmedeisis.bugstick.Joystick
import com.jmedeisis.bugstick.JoystickListener
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class HomeFragment : FullFrameFragment() {

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
                (activity as MainActivity).send(data0)
            }

            override fun onUp() {
            }
        })
        return root
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