package com.example.omegajoy.ui.code

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omegajoy.data.database.AppRoomDatabase
import com.example.omegajoy.data.entities.Preset
import com.example.omegajoy.data.entities.PresetCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CodeViewModel(dataSource: AppRoomDatabase) : ViewModel() {
    private val categoryDao = dataSource.categoryDao()
    private val commandDao = dataSource.commandDao()
    private val presetCommandDao = dataSource.presetCommandDao()
    private val presetDao = dataSource.presetDao()
    val commandDataDao = dataSource.commandDataDao()

    private val _categoryList = MutableLiveData<CategoryList>()
    val categoryList: LiveData<CategoryList> = _categoryList
    private val _commandList = MutableLiveData<CommandList>()
    val commandList: LiveData<CommandList> = _commandList
    private val _presetList = MutableLiveData<List<PresetItem>>()
    val presetItem: LiveData<List<PresetItem>> = _presetList
    lateinit var presetButtonNow: String

    fun preLoad() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = CategoryList(names = categoryDao.getAll().map { it.name })
            _categoryList.postValue(
                result
            )
            switchPresetByButtonName()
        }
    }

    fun loadCommandsByCategoryName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = CommandList(names = commandDao.getByCategoryName(name).map { it.name })
            _commandList.postValue(
                result
            )
        }
    }

    fun addCommandToPreset(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO[Bug]: если нет пресета в БД с данным именем кнопки, то выходит ошибка
            var preset = presetDao.getPresetByButtonName(presetButtonNow)
            if (preset == null) {
                presetDao.insert(Preset(name = "", attachedButton = presetButtonNow))
                preset = presetDao.getPresetByButtonName(presetButtonNow)
            }
            val command = commandDao.getByName(name)
            val latestPosition =
                presetCommandDao.getLatestPositionByButtonName(presetButtonNow).max()
            val position = if (latestPosition == null) 0 else latestPosition + 1
            presetCommandDao.insert(
                PresetCommand(
                    commandId = command.id, presetId = preset.id,
                    position = position
                )
            )
            switchPresetByButtonName()
        }
    }

    fun removeCommandFromPreset(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            presetCommandDao.deleteByPosition(presetButtonNow, position)
            val list = presetCommandDao.getAllByButtonName(presetButtonNow)
            var i = 0
            list.sortedBy { it.position }.forEach {
                it.position = i
                presetCommandDao.update(it)
                i += 1
            }
            switchPresetByButtonName()
        }
    }

    fun switchPresetByButtonName() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val commands = presetDao.getCommandsByButtonName(presetButtonNow)
                val presetCommands =
                    presetCommandDao.getAllByButtonName(presetButtonNow).toMutableList()
                _presetList.postValue(
                    commands.map {
                        val command = it
                        val data = commandDataDao.getDataByCommandId(it.id)
                        val position = presetCommands.first().position
                        val id = presetCommands.first().id
                        presetCommands.removeAt(0)
                        PresetItem(
                            id = id,
                            command = command,
                            data = data,
                            position = position
                        )
                    }
                )
            } catch (error: Exception) {
                Log.e("[SwitchPreset]", error.toString())
            }
        }
    }
}