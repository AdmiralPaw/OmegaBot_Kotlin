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

        val button_to_code: ImageButton = root.findViewById(R.id.button_to_code)
        button_to_code.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_code)
        )
        val button_to_menu: ImageButton = root.findViewById(R.id.button_to_menu)
        button_to_menu.setOnClickListener {
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
                )!!) {
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
        val angle: Int
        if (degrees < 0)
            angle = 360 + degrees.toInt()
        else
            angle = degrees.toInt()
        return angle
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
    fun convertJoystickToDrive(angel: Int, offset: Int): ByteArray? {
        val data = ByteArray(2)
        //TODO добавить ссылку на объяснение типа данного движения
        val x = Math.floor(offset * Math.cos(Math.toRadians(angel.toDouble())))
            .toInt()
        val y = Math.floor(offset * Math.sin(Math.toRadians(angel.toDouble())))
            .toInt()
        var left_engine = 0
        var right_engine = 0
        var left = 1
        var right = 1
        val rotate_speed = Math.abs(x)
        if (x > 0) {
            left_engine = y + rotate_speed
            right_engine = y - rotate_speed
        } else if (x < 0) {
            left_engine = y - rotate_speed
            right_engine = y + rotate_speed
        } else {
            left_engine = y
            right_engine = y
        }
        //ПРОТОКОЛ диктует скорость от -100 до 100, где впоследствии преобразуется
        // в промежуток от 0 до 100 со значением направления движения
        if (left_engine > 100) left_engine = 100
        if (left_engine < -100) left_engine = -100
        if (right_engine > 100) right_engine = 100
        if (right_engine < -100) right_engine = -100
        if (left_engine < 0) {
            left_engine = Math.abs(left_engine)
            left = 0
        }
        if (right_engine < 0) {
            right_engine = Math.abs(right_engine)
            right = 0
        }
        //ПРОТОКОЛ диктует ставить направление движения (вперед - 1, назад - 0)
        // в старший бит байта, остальные 7-мь бит заполнять значением скорости от 0 до 100
        data[0] = (left shl 7 or (left_engine and 0xFF)).toByte()
        data[1] = (right shl 7 or (right_engine and 0xFF)).toByte()
        return data
    }
}