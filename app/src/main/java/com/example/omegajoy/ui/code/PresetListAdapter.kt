package com.example.omegajoy.ui.code

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.omegajoy.R
import com.example.omegajoy.data.dao.UserData
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*

class PresetListAdapter(
    private var codeFragment: CodeFragment
) : ListAdapter<PresetItem, PresetListAdapter.ViewHolder>(DIFF_CALLBACK), ItemTouchHelperAdapter {

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
        var dataList: List<UserData>? = null
        var id0: Int? = null

        fun bind(item: PresetItem) = with(itemView) {
            // TODO: Bind the data with View
            button?.text = item.command.name
            positionNumber?.text = item.position.toString()
            if (id0 == null)
                addDataToBindingView(item)
            else if (id0 != item.id) {
                commandData?.removeAllViews()
                addDataToBindingView(item)
            }

            deleteButton?.setOnClickListener {
                codeFragment.removeCommandFromPreset((positionNumber?.text as String).toInt())
            }
        }

        private fun addDataToBindingView(item: PresetItem) {
            val inflater = codeFragment.layoutInflater
            item.data.forEach {
                val commandDataItem: LinearLayout = when (it.type) {
                    "seekBar" -> inflater.inflate(R.layout.command_data_item_seekbar, null)
                    "toggle" -> inflater.inflate(R.layout.command_data_item_toogle, null)
                    else -> inflater.inflate(R.layout.command_data_item, null)
                } as LinearLayout
                when (it.type) {
                    "seekBar" -> {
                        commandDataItem.findViewById<TextView>(R.id.text).text = it.name
                        val seekbar = commandDataItem.findViewById<Slider>(R.id.seek_bar)
                        seekbar.valueFrom = it.valueMin!!.toFloat()
                        seekbar.valueTo = it.valueMax!!.toFloat()
                        seekbar.value = it.data.toFloat()
                        seekbar.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                            override fun onStartTrackingTouch(slider: Slider) {
                                // Responds to when slider's touch event is being started
                            }

                            override fun onStopTrackingTouch(slider: Slider) {
                                codeFragment.updateDataById(it.id, slider.value.toString())
                            }
                        })
                    }
                    "toggle" -> {
                        val toggle =
                            commandDataItem.findViewById<SwitchMaterial>(R.id.switch_toggle)
                        toggle.text = it.name
                        toggle.isChecked = it.data.toInt() == 1
                        toggle.setOnCheckedChangeListener { _, b ->
                            when (b) {
                                true -> codeFragment.updateDataById(it.id, "1")
                                false -> codeFragment.updateDataById(it.id, "0")
                            }
                        }
                    }
                    else -> {
                        commandDataItem.findViewById<TextView>(R.id.first).text = it.name
                        commandDataItem.findViewById<TextView>(R.id.second).text = it.type
                    }
                }
                commandData?.addView(commandDataItem)
                id0 = item.id
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
                    return oldItem.id == newItem.id && oldItem.position == newItem.position
                }

                override fun areContentsTheSame(oldItem: PresetItem, newItem: PresetItem): Boolean {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldItem.command == newItem.command &&
                            oldItem.data == newItem.data
                }
            }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                val temp = currentList.toMutableList()
                Collections.swap(temp, i, i + 1)
                val valTemp = temp[i].position
                temp[i].position = temp[i + 1].position
                temp[i + 1].position = valTemp
                onCurrentListChanged(currentList, temp)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                val temp = currentList.toMutableList()
                Collections.swap(temp, i, i - 1)
                val valTemp = temp[i].position
                temp[i].position = temp[i - 1].position
                temp[i - 1].position = valTemp
                onCurrentListChanged(currentList, temp)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        val temp = currentList.toMutableList()
        temp.removeAt(position)
        submitList(temp)
        notifyItemRemoved(position)
    }
}

class SimpleItemTouchHelperCallback(private val adapter: ItemTouchHelperAdapter) :
    ItemTouchHelper.Callback() {
    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = 0
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

}

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}