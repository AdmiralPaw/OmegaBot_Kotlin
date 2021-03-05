package com.example.omegajoy.ui.code

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.omegajoy.R
import com.example.omegajoy.data.entities.Data

class PresetListAdapter(
    private var codeFragment: CodeFragment
) : ListAdapter<PresetItem, PresetListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recylerview_preset_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var button: Button? = null
        var deleteButton: ImageButton? = null
        var positionNumber: TextView? = null
        var commandData: LinearLayout? = null
        var dataList: List<Data>? = null
        var id0: Int? = null

        fun bind(item: PresetItem) = with(itemView) {
            // TODO: Bind the data with View
            button?.text = item.command.name
            positionNumber?.text = item.position.toString()
            if (id0 == null)
                item.data.forEach {
                    val commandDataItem: LinearLayout =
                        codeFragment.layoutInflater.inflate(
                            R.layout.command_data_item,
                            null
                        ) as LinearLayout
                    commandDataItem.findViewById<TextView>(R.id.first).text = it.name
                    commandDataItem.findViewById<TextView>(R.id.second).text = it.type
                    commandData?.addView(commandDataItem)
                    id0 = item.id
                }
            else if (id0 != item.id) {
                commandData?.removeAllViews()
                item.data.forEach {
                    val commandDataItem: LinearLayout =
                        codeFragment.layoutInflater.inflate(
                            R.layout.command_data_item,
                            null
                        ) as LinearLayout
                    commandDataItem.findViewById<TextView>(R.id.first).text = it.name
                    commandDataItem.findViewById<TextView>(R.id.second).text = it.type
                    commandData?.addView(commandDataItem)
                    id0 = item.id
                }
            }

            deleteButton?.setOnClickListener {
                codeFragment.removeCommandFromPreset((positionNumber?.text as String).toInt())
            }
        }

        init {
            button = itemView.findViewById(R.id.button_preset_item)
            deleteButton = itemView.findViewById(R.id.button_delete_command)
            positionNumber = itemView.findViewById(R.id.command_number)
            commandData = itemView.findViewById(R.id.command_data)
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<PresetItem> =
            object : DiffUtil.ItemCallback<PresetItem>() {
                override fun areItemsTheSame(oldItem: PresetItem, newItem: PresetItem): Boolean {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: PresetItem, newItem: PresetItem): Boolean {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return false
                }
            }
    }
}