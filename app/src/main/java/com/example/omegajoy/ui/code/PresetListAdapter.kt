package com.example.omegajoy.ui.code

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.omegajoy.R
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*


class PresetListAdapter(
    private var values: MutableList<PresetItem>,
    private var codeFragment: CodeFragment
) : RecyclerView.Adapter<PresetListAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recylerview_preset_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var button: Button? = null
        var deleteButton: ImageButton? = null
        var positionNumber: TextView? = null
        var commandData: LinearLayout? = null
        var inflater: LayoutInflater? = null
        var dataToggle: Boolean = true

        fun bind(item: PresetItem) = with(itemView) {
            button?.text = item.command.name
            positionNumber?.text = item.position.toString()
            commandData?.removeAllViews()
            dataToggle = true


            button?.setOnClickListener {
                if (dataToggle)
                    addDataToBindingView(item)
                else
                    commandData?.removeAllViews()
                dataToggle = dataToggle == false
            }
            deleteButton?.setOnClickListener {
                codeFragment.removeCommandFromPreset(item.position)

            }
        }

        private fun addDataToBindingView(item: PresetItem) {
            item.data.forEach { userData ->
//                val commandDataItem: LinearLayout = when (it.type) {
//                    "seekBar" -> itemWithSeekBar
//                    "toggle" -> itemWithToggle
//                    else -> itemBlank
//                } as LinearLayout
                val dataView = when (userData.type) {
                    "seekBar" -> {
                        val layout =
                            inflater!!.inflate(R.layout.command_data_item_seekbar, null)
                        layout.findViewById<TextView>(R.id.text).text = userData.name
                        val seekbar = layout.findViewById<Slider>(R.id.seek_bar)
                        seekbar.valueFrom = userData.valueMin!!.toFloat()
                        seekbar.valueTo = userData.valueMax!!.toFloat()
                        seekbar.value = userData.data.toFloat()
                        seekbar.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                            override fun onStartTrackingTouch(slider: Slider) {
                                // Responds to when slider's touch event is being started
                            }

                            override fun onStopTrackingTouch(slider: Slider) {
                                codeFragment.updateDataById(
                                    userData.id,
                                    slider.value.toInt().toString()
                                )
                                userData.data = slider.value.toInt().toString()
                            }
                        })
                        layout
                    }
                    "toggle" -> {
                        val layout = inflater!!.inflate(R.layout.command_data_item_toogle, null)
                        val toggle =
                            layout.findViewById<SwitchMaterial>(R.id.switch_toggle)
                        toggle.text = userData.name
                        toggle.isChecked = when (userData.data) {
                            "true" -> true
                            else -> false
                        }
                        toggle.setOnCheckedChangeListener { _, b ->
                            userData.data = when (b) {
                                true -> "true"
                                false -> "false"
                            }
                            codeFragment.updateDataById(userData.id, userData.data)
                        }
                        layout
                    }
                    else -> {
                        val layout = inflater!!.inflate(R.layout.command_data_item, null)
                        layout.findViewById<TextView>(R.id.first).text = userData.name
                        layout.findViewById<TextView>(R.id.second).text = userData.type
                        layout
                    }
                }
                commandData?.addView(dataView)
            }
        }

        init {
            button = itemView.findViewById(R.id.button_preset_item)
            deleteButton = itemView.findViewById(R.id.button_delete_command)
            positionNumber = itemView.findViewById(R.id.command_number)
            commandData = itemView.findViewById(R.id.command_data)
            inflater = LayoutInflater.from(commandData?.context)
        }
    }


//    companion object {
//        val DIFF_CALLBACK: DiffUtil.ItemCallback<PresetItem> =
//            object : DiffUtil.ItemCallback<PresetItem>() {
//                override fun areItemsTheSame(oldItem: PresetItem, newItem: PresetItem): Boolean {
//                    // User properties may have changed if reloaded from the DB, but ID is fixed
//                    return oldItem.id == newItem.id && oldItem.position == newItem.position
//                }
//
//                override fun areContentsTheSame(oldItem: PresetItem, newItem: PresetItem): Boolean {
//                    // NOTE: if you use equals, your object must properly override Object#equals()
//                    // Incorrectly returning false here will result in too many animations.
//                    return oldItem.command.id == newItem.command.id
//                }
//            }
//    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition != toPosition) {
//            if (fromPosition < toPosition) {
//                for (i in fromPosition until toPosition) {
//                    Collections.swap(values, i, i + 1)
//                    val valTemp = values[i].position
//                    values[i].position = values[i + 1].position
//                    values[i + 1].position = valTemp
////                onCurrentListChanged(currentList, temp)
////                submitList(temp)
//                }
//            } else {
//                for (i in fromPosition downTo toPosition + 1) {
//                    Collections.swap(values, i, i - 1)
//                    val valTemp = values[i].position
//                    values[i].position = values[i - 1].position
//                    values[i - 1].position = valTemp
////                onCurrentListChanged(currentList, temp)
////                submitList(temp)
//                }
//            }
            Collections.swap(values, fromPosition, toPosition)
            val valTemp = values[fromPosition].position
            values[fromPosition].position = values[toPosition].position
            values[toPosition].position = valTemp
            notifyItemMoved(fromPosition, toPosition)
            notifyItemChanged(fromPosition, false)
            notifyItemChanged(toPosition, false)
            codeFragment.codeViewModel.onItemMove(values[fromPosition], values[toPosition])
        }
    }

    override fun onItemDismiss(position: Int) {
        val temp = values.toMutableList()
        temp.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount() = values.size

    fun updateItem(it: PresetItem) {
        values.add(it)
        notifyItemChanged(it.position)
    }

    fun updateAll(it: List<PresetItem>) {
        values.clear()
        values.addAll(it)
        notifyDataSetChanged()
    }
}

class SimpleItemTouchHelperCallback(
    private val adapter: ItemTouchHelperAdapter,
    private val context: Context
) :
    ItemTouchHelper.Callback() {
    override fun isLongPressDragEnabled(): Boolean {
        val v = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        val milliseconds = 125L
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val amplitude = VibrationEffect.DEFAULT_AMPLITUDE
            v.vibrate(VibrationEffect.createOneShot(milliseconds, amplitude))
        } else {
            //deprecated in API 26
            v.vibrate(milliseconds)
        }
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