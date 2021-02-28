package com.example.omegajoy.ui.code

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.omegajoy.R

class CategoryListAdapter(private val values: List<String>) :
    RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recylerview_category_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.button?.text = values[position]
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var button: Button? = null

        init {
            button = itemView.findViewById(R.id.button_category_item)
        }
    }
}