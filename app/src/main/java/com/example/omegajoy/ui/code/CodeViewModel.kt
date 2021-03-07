package com.example.omegajoy.ui.code

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omegajoy.data.database.AppRoomDatabase
import com.example.omegajoy.data.entities.Preset
import com.example.omegajoy.data.entities.PresetCommand
import com.example.omegajoy.data.entities.PresetCommandData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CodeViewModel(dataSource: AppRoomDatabase) : ViewModel() {
    private val categoryDao = dataSource.categoryDao()
    private val commandDao = dataSource.commandDao()
    private val presetCommandDao = dataSource.presetCommandDao()
    private val presetDao = dataSource.presetDao()
    val commandDataDao = dataSource.commandDataDao()
    val presetCommandDataDao = dataSource.presetCommandDataDao()
    val dataDao = dataSource.dataDao()

    private val _categoryList = MutableLiveData<CategoryList>()
    val categoryList: LiveData<CategoryList> = _categoryList
    private val _commandList = MutableLiveData<CommandList>()
    val commandList: LiveData<CommandList> = _commandList
    private val _presetList = MutableLiveData<List<PresetItem>>()
    val presetList: LiveData<List<PresetItem>> = _presetList
    lateinit var presetButtonNow: String
    private val _presetItem = MutableLiveData<PresetItem>()
    val presetItem: LiveData<PresetItem> = _presetItem
    val presetWorker = PresetWorker()

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
            val presetCommandId = presetCommandDao.insert(
                PresetCommand(
                    commandId = command.id, presetId = preset.id,
                    position = position
                )
            )
            commandDataDao.getByCommandId(command.id).map {
                val data = dataDao.getById(it.dataId)
                presetCommandDataDao.insert(
                    PresetCommandData(
                        presetCommandId = presetCommandId.toInt(),
                        commandDataId = it.id,
                        data = data.defaultData
                    )
                )
            }
            switchPresetByButtonName()
        }
    }

    fun removeCommandFromPreset(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                presetCommandDao.getAllByButtonName(presetButtonNow).filter {
                    it.position == position
                }.forEach {
                    presetCommandDataDao.deleteByPresetCommandId(it.id)
                    presetCommandDao.deleteById(it.id)
                }
                val list = presetCommandDao.getAllByButtonName(presetButtonNow)
                var i = 0
                list.sortedBy { it.position }.forEach {
                    it.position = i
                    presetCommandDao.update(it)
                    i += 1
                }
                switchPresetByButtonName()
            } catch (error: Exception) {
                Log.e("[removeFromPreset]", error.toString())
            }
        }
    }

    fun switchPresetByButtonName() {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                _presetList.postValue(presetWorker.loadPreset(presetButtonNow))
            } catch (error: Exception) {
                Log.e("[loadPreset]", error.toString())
            }
        }
    }

    fun onItemMove(item1: PresetItem, item2: PresetItem) {
        viewModelScope.launch(Dispatchers.IO) {
            presetWorker.movePresetCommand(item1, item2)
        }
    }

    fun updateDataById(id: Int, data: String) {
        viewModelScope.launch(Dispatchers.IO) {
            presetCommandDataDao.updateDataById(id, data)
        }
    }

    suspend fun lazyLoadPreset(presetButtonName: String) {
        val presetCommands =
            presetCommandDao.getAllByButtonName(presetButtonName).sortedBy { it.position }

        presetCommands.forEach { presetCommand ->
            val command = commandDao.getById(presetCommand.commandId)
            val presetCommandData = presetCommandDataDao.getByPresetCommandId(presetCommand.id)
            val data = presetCommandData.map {
                val commandData = commandDataDao.getById(it.commandDataId)
                val dataInfo = dataDao.getById(commandData.dataId)
                UserData(
                    id = dataInfo.id,
                    name = dataInfo.name,
                    type = dataInfo.type,
                    valueMin = dataInfo.valueMin,
                    valueMax = dataInfo.valueMax,
                    data = it.data,
                    position = commandData.position
                )
            }.sortedBy { it.position }


            _presetItem.postValue(
                PresetItem(
                    id = presetCommand.id,
                    position = presetCommand.position,
                    command = command,
                    data = data
                )
            )
        }
    }


    inner class PresetWorker {
        /**
         * version_1 - работает долго
         * Загружает пресет с элементами для работы recycler_view по имени кнопки
         * @param [presetButtonName] имя кнопки, по которой берутся команды из БД
         * в таблице [PresetCommand]
         * @return list объектов класса [PresetItem].
         */
        suspend fun loadPreset(presetButtonName: String): List<PresetItem> {
            val presetCommands =
                presetCommandDao.getAllByButtonName(presetButtonName).sortedBy { it.position }

            return presetCommands.map { presetCommand ->
                val command = commandDao.getById(presetCommand.commandId)
                val presetCommandData = presetCommandDataDao.getByPresetCommandId(presetCommand.id)
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
        }

        suspend fun movePresetCommand(item1: PresetItem, item2: PresetItem) {
            val presetCommand1 = item1.toPresetCommandNoPresetId()
            val presetCommand2 = item2.toPresetCommandNoPresetId()
            val presetId = presetCommandDao.getPresetIdById(presetCommand1.id)
            presetCommand1.presetId = presetId
            presetCommand2.presetId = presetId
            presetCommandDao.update(presetCommand1)
            presetCommandDao.update(presetCommand2)
        }

        /**
         * Обновляет
         * @return
         */
        fun updatePreset() {

        }
    }
}
