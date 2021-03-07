package com.example.omegajoy.ui.code

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.omegajoy.R
import com.google.android.material.button.MaterialButton

class CategoryListAdapter(
    private val values: List<String>,
    private var codeFragment: CodeFragment
) :
    RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recylerview_category_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.button?.text = values[position]
        holder.button?.setOnClickListener {
            codeFragment.setupCommands((it as MaterialButton).text.toString())
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var button: MaterialButton? = null

        init {
            button = itemView.findViewById(R.id.button_category_item)
        }
    }
}