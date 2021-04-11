package com.example.omegabot.ui.robotmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.omegabot.MainActivity
import com.example.omegabot.R
import com.example.omegabot.ui.FullFrameFragment
import com.example.omegabot.ui.robotmanager.add_robot.AddRobotModelFactory
import com.example.omegabot.ui.robotmanager.add_robot.AddRobotViewModel

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class RobotManagerFragment : FullFrameFragment() {
    val robotViewModel: AddRobotViewModel by viewModels {
        AddRobotModelFactory((activity as MainActivity).database, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_robot_manager, container, false)

        val button_to_home: ImageButton = root.findViewById(R.id.button_to_home)
        button_to_home.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_nav_robot_manager_to_nav_home)
        )
        val button_to_menu: ImageButton = root.findViewById(R.id.button_to_menu)
        button_to_menu.setOnClickListener {
            (activity as MainActivity).openDrawer()
        }
        val buttonAdd: Button = root.findViewById(R.id.button_add_device)
        buttonAdd.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_nav_robot_manager_to_nav_add_robot)
        )

        val recyclerRobotInternet: RecyclerView = root.findViewById(R.id.recycler_robot_internet)
        recyclerRobotInternet.layoutManager = LinearLayoutManager(activity)
        recyclerRobotInternet.adapter = RobotListAdapter(listOf(), this)

        robotViewModel.robotsInternet.observe(viewLifecycleOwner, Observer {
            val robotsList = it ?: return@Observer

            recyclerRobotInternet.adapter = RobotListAdapter(robotsList, this)
        })

        robotViewModel.getRobotsByConnectionType("internet")

        return root
    }
}