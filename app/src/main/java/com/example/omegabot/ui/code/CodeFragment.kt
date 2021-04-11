package com.example.omegabot.ui.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.omegabot.MainActivity
import com.example.omegabot.R
import com.example.omegabot.ui.FullFrameFragment
import com.google.android.material.button.MaterialButton

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class CodeFragment : FullFrameFragment() {
    private lateinit var presetButtonNow: String
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var commandRecyclerView: RecyclerView
    private lateinit var presetRecyclerView: RecyclerView
    private lateinit var buttonPresetLeft: MaterialButton
    private lateinit var buttonPresetTop: MaterialButton
    private lateinit var buttonPresetBottom: MaterialButton
    private lateinit var buttonPresetRight: MaterialButton
    private lateinit var buttonPresetBlank: MaterialButton

    val codeViewModel: CodeViewModel by viewModels {
        CodeViewModelFactory((activity as MainActivity).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_code, container, false)
        presetButtonNow = (activity as MainActivity).latestPresetButton
        val button_to_home: ImageButton = root.findViewById(R.id.button_to_home)
        button_to_home.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_nav_code_to_nav_home)
        )
        val button_to_menu: ImageButton = root.findViewById(R.id.button_to_menu)
        button_to_menu.setOnClickListener {
            (activity as MainActivity).openDrawer()
        }

        buttonPresetLeft = root.findViewById(R.id.button_preset_left)
        buttonPresetTop = root.findViewById(R.id.button_preset_top)
        buttonPresetBottom = root.findViewById(R.id.button_preset_bottom)
        buttonPresetRight = root.findViewById(R.id.button_preset_right)
        buttonPresetBlank = root.findViewById(R.id.button_preset_blank)
        categoryRecyclerView = root.findViewById(R.id.recyclerview_categories)
        commandRecyclerView = root.findViewById(R.id.recyclerview_commands)
        presetRecyclerView = root.findViewById(R.id.recyclerview_preset_now)

        commandRecyclerView.layoutManager = LinearLayoutManager(activity)
        categoryRecyclerView.layoutManager = LinearLayoutManager(activity)
        presetRecyclerView.layoutManager = LinearLayoutManager(activity)

        commandRecyclerView.adapter = CategoryListAdapter(listOf(), this)
        codeViewModel.categoryList.observe(viewLifecycleOwner, Observer {
            val categoryList = it ?: return@Observer

            categoryRecyclerView.adapter =
                CategoryListAdapter(categoryList.names, this)
        })

        commandRecyclerView.adapter = CommandListAdapter(listOf(), this)
        codeViewModel.commandList.observe(viewLifecycleOwner, Observer {
            val commandList = it ?: return@Observer

            commandRecyclerView.adapter =
                CommandListAdapter(commandList.names, this)
        })

        presetRecyclerView.adapter = PresetListAdapter(mutableListOf(), this)
        val callback =
            SimpleItemTouchHelperCallback(
                presetRecyclerView.adapter as PresetListAdapter,
                (activity as MainActivity).applicationContext
            )
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(presetRecyclerView)

        codeViewModel.presetList.observe(viewLifecycleOwner, Observer {
            (presetRecyclerView.adapter as PresetListAdapter).updateAll(it)
        })

        setupButtons()
        codeViewModel.preLoad()

        return root
    }

    private fun setupButtons() {
        codeViewModel.presetButtonNow = presetButtonNow

        buttonPresetLeft.isChecked = buttonPresetLeft.contentDescription == presetButtonNow
        buttonPresetTop.isChecked = buttonPresetTop.contentDescription == presetButtonNow
        buttonPresetBottom.isChecked = buttonPresetBottom.contentDescription == presetButtonNow
        buttonPresetRight.isChecked = buttonPresetRight.contentDescription == presetButtonNow
        buttonPresetBlank.isChecked = buttonPresetBlank.contentDescription == presetButtonNow

        buttonPresetLeft.addOnCheckedChangeListener { button, isChecked ->
            (buttonClickAction(
                button,
                isChecked
            ))
        }
        buttonPresetTop.addOnCheckedChangeListener { button, isChecked ->
            (buttonClickAction(
                button,
                isChecked
            ))
        }
        buttonPresetBottom.addOnCheckedChangeListener { button, isChecked ->
            (buttonClickAction(
                button,
                isChecked
            ))
        }
        buttonPresetRight.addOnCheckedChangeListener { button, isChecked ->
            (buttonClickAction(
                button,
                isChecked
            ))
        }
        buttonPresetBlank.addOnCheckedChangeListener { button, isChecked ->
            (buttonClickAction(
                button,
                isChecked
            ))
        }
    }

    private fun buttonClickAction(view: MaterialButton, isChecked: Boolean) {
        if (isChecked) {
            presetButtonNow = view.contentDescription.toString()
            codeViewModel.presetButtonNow = presetButtonNow
            (activity as MainActivity).latestPresetButton = presetButtonNow
            codeViewModel.switchPresetByButtonName()
        }
    }

    fun setupCommands(name: String) {
        codeViewModel.loadCommandsByCategoryName(name)
    }

    fun addCommandToPreset(name: String) {
        codeViewModel.addCommandToPreset(name)
    }

    fun removeCommandFromPreset(position: Int) {
        codeViewModel.removeCommandFromPreset(position)
    }

    fun updateDataById(id: Int, data: String) {
        codeViewModel.updateDataById(id, data)
    }
}
