package com.example.omegajoy.ui.home

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omegajoy.data.database.AppRoomDatabase
import com.example.omegajoy.ui.code.PresetItem
import com.example.omegajoy.ui.code.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(dataSource: AppRoomDatabase) : ViewModel() {
    private val categoryDao = dataSource.categoryDao()
    private val commandDao = dataSource.commandDao()
    private val presetCommandDao = dataSource.presetCommandDao()
    private val presetDao = dataSource.presetDao()
    val commandDataDao = dataSource.commandDataDao()
    val presetCommandDataDao = dataSource.presetCommandDataDao()
    val dataDao = dataSource.dataDao()

    private val _presetList = MutableLiveData<List<PresetList>>()
    val presetList: LiveData<List<PresetList>> = _presetList

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
}