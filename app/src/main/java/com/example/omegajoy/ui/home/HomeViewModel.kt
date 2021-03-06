package com.example.omegajoy.ui.home

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omegajoy.data.database.AppRoomDatabase
import com.example.omegajoy.data.entities.Command
import com.example.omegajoy.ui.code.PresetItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(dataSource: AppRoomDatabase) : ViewModel() {
    private val presetCommandDao = dataSource.presetCommandDao()
    private val presetDao = dataSource.presetDao()
    private val presetCommandDataDao = dataSource.presetCommandDataDao()

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
                val temp = mutableListOf<PresetList>()
                for (presetButtonName in presetButtonNames) {
                    val commands = presetDao.getCommandsByButtonName(presetButtonName)
                    val presetCommands =
                        presetCommandDao.getAllByButtonName(presetButtonName).toMutableList()
                    val rowPreset = commands.map {
                        val data = presetCommandDataDao.getDataByCommandId(it.id, it.position)
                        val position = presetCommands.first().position
                        val id = presetCommands.first().id
                        presetCommands.removeAt(0)
                        PresetItem(
                            id = id,
                            command = Command(it.id, it.name, it.categoryId),
                            data = data,
                            position = position
                        )
                    }
                    temp.add(PresetList(currentList = rowPreset, attachedButton = presetButtonName))
                }
                _presetList.postValue(temp)
            } catch (error: Exception) {
                Log.e("[getPreset]", error.toString())
            }
        }
    }
}