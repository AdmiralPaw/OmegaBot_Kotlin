package com.example.omegabot.ui.robotmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omegabot.MainActivity
import com.example.omegabot.R
import com.example.omegabot.data.entities.Robot

class RobotListAdapter(
    private var values: List<Robot>,
    private var fragment: RobotManagerFragment
) : RecyclerView.Adapter<RobotListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_robot_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var button: Button? = null
        var textName: TextView? = null
        var textId: TextView? = null

        fun bind(item: Robot) = with(itemView) {
            textName?.text = item.name
            textId?.text = item.uuid
            button?.setOnClickListener {
                (fragment.activity as MainActivity).changeRobot(textId?.text.toString())
            }
        }

        init {
            button = itemView.findViewById(R.id.button_connect_to_robot)
            textName = itemView.findViewById(R.id.text_name)
            textId = itemView.findViewById(R.id.text_id_robot)
        }
    }

    override fun getItemCount() = values.size
}