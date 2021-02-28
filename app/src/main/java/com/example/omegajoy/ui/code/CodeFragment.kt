package com.example.omegajoy.ui.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.omegajoy.MainActivity
import com.example.omegajoy.R
import com.example.omegajoy.data.dao.CategoryDao
import com.example.omegajoy.ui.FullFrameFragment

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class CodeFragment : FullFrameFragment() {
    private lateinit var presetButtonNow: String
    private lateinit var categoryRecyclerView: RecyclerView
    private val codeViewModel: CodeViewModel by viewModels {
        CodeViewModelFactory((activity as MainActivity).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_code, container, false)

        val button_to_home: ImageButton = root.findViewById(R.id.button_to_home)
        button_to_home.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_nav_code_to_nav_home)
        )
        val button_to_menu: ImageButton = root.findViewById(R.id.button_to_menu)
        button_to_menu.setOnClickListener {
            (activity as MainActivity).openDrawer()
        }

        val buttonPresetLeft: ImageButton = root.findViewById(R.id.button_preset_left)
        val buttonPresetTop: ImageButton = root.findViewById(R.id.button_preset_right)
        val buttonPresetBottom: ImageButton = root.findViewById(R.id.button_preset_bottom)
        val buttonPresetRight: ImageButton = root.findViewById(R.id.button_preset_right)
        val buttonPresetBlank: ImageButton = root.findViewById(R.id.button_preset_blank)
        categoryRecyclerView = root.findViewById(R.id.recyclerview_categories)

        codeViewModel.categoryList.observe(viewLifecycleOwner, Observer {
            val categoryList = it ?: return@Observer

            categoryRecyclerView.layoutManager = LinearLayoutManager(activity)
            categoryRecyclerView.adapter =
                CategoryListAdapter(categoryList.names)
        })

        codeViewModel.preLoad()
        presetButtonNow = buttonPresetBlank.id.toString()

        return root
    }

    private fun setupRecyclerViews(categoryDao: CategoryDao) {
        categoryRecyclerView.layoutManager = LinearLayoutManager(activity)
        categoryRecyclerView.adapter =
            CategoryListAdapter(categoryDao.getAll().map { "${it.name}" })
    }
}
