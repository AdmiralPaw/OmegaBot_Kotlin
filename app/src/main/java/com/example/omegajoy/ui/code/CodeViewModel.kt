package com.example.omegajoy.ui.code

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omegajoy.data.database.AppRoomDatabase
import com.example.omegajoy.data.entities.CommandAndPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CodeViewModel(dataSource: AppRoomDatabase) : ViewModel() {
    val categoryDao = dataSource.categoryDao()
    val commandDao = dataSource.commandDao()
    val commandAndPresetDao = dataSource.commandAndPresetDao()
    val presetDao = dataSource.presetDao()

    private val _categoryList = MutableLiveData<CategoryList>()
    val categoryList: LiveData<CategoryList> = _categoryList
    private val _commandList = MutableLiveData<CommandList>()
    val commandList: LiveData<CommandList> = _commandList
    private val _presetList = MutableLiveData<PresetList>()
    val presetList: LiveData<PresetList> = _presetList
    lateinit var presetButtonNow: String

    fun preLoad() {
        viewModelScope.launch(Dispatchers.IO) {
            _categoryList.postValue(
                CategoryList(names = categoryDao.getAll().map { "${it.name}" })
            )
            switchPresetByButtonName()
        }
    }

    fun loadCommandsByCategoryName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _commandList.postValue(
                CommandList(names = commandDao.getByCategoryName(name).map { "${it.name}" })
            )
        }
    }

    fun addCommandToPreset(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val preset = presetDao.getPresetByButtonName(presetButtonNow)
            val command = commandDao.getByName(name)
            val latestPosition =
                commandAndPresetDao.getLatestPositionByButtonName(presetButtonNow).max()
            val position = if (latestPosition == null) 0 else latestPosition + 1
            commandAndPresetDao.insert(
                CommandAndPreset(
                    command_id = command.id, preset_id = preset.id,
                    position = position
                )
            )
            switchPresetByButtonName()
        }
    }

    fun removeCommandFromPreset(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            commandAndPresetDao.deleteByPosition(presetButtonNow, position)
            val list = commandAndPresetDao.getAllByButtonName(presetButtonNow)
            var i = 0
            list.sortedBy { it.position }.forEach {
                it.position = i
                commandAndPresetDao.update(it)
                i += 1
            }
            switchPresetByButtonName()
        }
    }

    fun switchPresetByButtonName() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = presetDao.getCommandsByButtonName(presetButtonNow)
            _presetList.postValue(
                PresetList(commands = list)
            )
        }
    }
}