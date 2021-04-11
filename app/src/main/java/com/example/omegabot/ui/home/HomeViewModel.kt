package com.example.omegabot.ui.home

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omegabot.data.database.AppRoomDatabase
import com.example.omegabot.ui.code.PresetItem
import com.example.omegabot.ui.code.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class HomeViewModel(dataSource: AppRoomDatabase, val homeFragment: HomeFragment) : ViewModel() {
    private val categoryDao = dataSource.categoryDao()
    private val commandDao = dataSource.commandDao()
    private val presetCommandDao = dataSource.presetCommandDao()
    private val presetDao = dataSource.presetDao()
    val commandDataDao = dataSource.commandDataDao()
    val presetCommandDataDao = dataSource.presetCommandDataDao()
    val dataDao = dataSource.dataDao()

    private val _presetList = MutableLiveData<List<PresetList>>()
    val presetList: LiveData<List<PresetList>> = _presetList

    var lastAngle: Int = 0
    var lastDistance: Int = 0
    var toggle: Boolean = false

    fun preLoad(buttons: List<View>) {
        val names = buttons.map {
            "${it.contentDescription}"
        }
        getPresetByButtonName(names)
    }

    fun getPresetByButtonName(presetButtonNames: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val temp = presetButtonNames.map { presetButtonName ->
                    val presetCommands =
                        presetCommandDao.getAllByButtonName(presetButtonName).toMutableList()
                    val rowPreset = presetCommands.map { presetCommand ->
                        val command = commandDao.getById(presetCommand.commandId)
                        val presetCommandData =
                            presetCommandDataDao.getByPresetCommandId(presetCommand.id)
                        val data = presetCommandData.map {
                            val commandData = commandDataDao.getById(it.commandDataId)
                            val dataInfo = dataDao.getById(commandData.dataId)
                            UserData(
                                id = it.id,
                                name = dataInfo.name,
                                type = dataInfo.type,
                                valueMin = dataInfo.valueMin,
                                valueMax = dataInfo.valueMax,
                                data = it.data,
                                position = commandData.position
                            )
                        }.sortedBy { it.position }

                        PresetItem(
                            id = presetCommand.id,
                            position = presetCommand.position,
                            command = command,
                            data = data
                        )
                    }
                    PresetList(
                        currentList = rowPreset,
                        attachedButton = presetButtonName
                    )
                }
                _presetList.postValue(temp)
            } catch (error: Exception) {
                Log.e("[getPreset]", error.toString())
            }
        }
    }

    fun sendData() {
        viewModelScope.launch(Dispatchers.IO) {
            while (toggle) {
                homeFragment.send(convertJoystickToDrive(lastAngle, lastDistance))
                delay(50L)
            }
        }
    }

    fun sendStoppers() {
        homeFragment.send(convertJoystickToDrive(0, 0))
    }

    /**
     * @author AdmPaw
     * @param angel угол от 0 до 360
     * @param offset отклонение от 0 до 100
     * @return два байта данных скорости и направления движения двигателей (гусениц) робота
     * в соответствии с ПРОТОКОЛОМ
     */
    fun convertJoystickToDrive(angel: Int, offset: Int): MutableList<Int> {
        val data = mutableListOf<Int>()
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
        leftEngine = (leftEngine * 2.55).toInt()
        rightEngine = (rightEngine * 2.55).toInt()

        //ПРОТОКОЛ диктует скорость от -100 до 100, где впоследствии преобразуется
        // в промежуток от 0 до 100 со значением направления движения
        if (leftEngine > 255) leftEngine = 255
        if (leftEngine < -255) leftEngine = -255
        if (rightEngine > 255) rightEngine = 255
        if (rightEngine < -255) rightEngine = -255
//        if (leftEngine < 0) {
//            leftEngine = abs(leftEngine)
//            left = 0
//        }
//        if (rightEngine < 0) {
//            rightEngine = abs(rightEngine)
//            right = 0
//        }
//        //ПРОТОКОЛ диктует ставить направление движения (вперед - 1, назад - 0)
//        // в старший бит байта, остальные 7-мь бит заполнять значением скорости от 0 до 100
//        data[0] = (left shl 7 or (leftEngine and 0xFF)).toByte()
//        data[1] = (right shl 7 or (rightEngine and 0xFF)).toByte()
        data.add(leftEngine)
        data.add(rightEngine)
        return data
    }
}