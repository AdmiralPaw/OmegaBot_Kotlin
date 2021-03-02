package com.example.omegajoy.ui.code

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omegajoy.R

class PresetListAdapter(private var values: List<String>, private var codeFragment: CodeFragment) :
    RecyclerView.Adapter<PresetListAdapter.ViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recylerview_preset_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.button?.text = values[position]
        holder.positionNumber?.text = position.toString()
        holder.deleteButton?.setOnClickListener {
            codeFragment.removeCommandFromPreset((holder.positionNumber?.text as String).toInt())
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var button: Button? = null
        var deleteButton: ImageButton? = null
        var positionNumber: TextView? = null

        init {
            button = itemView.findViewById(R.id.button_preset_item)
            deleteButton = itemView.findViewById(R.id.button_delete_command)
            positionNumber = itemView.findViewById(R.id.command_number)
        }
    }

    fun update(modelList: List<String>) {
        values = modelList.toMutableList()
        notifyDataSetChanged()
    }
}