package com.lutech.notepad.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lutech.notepad.R
import com.lutech.notepad.listener.CategoryItemClickListener
import com.lutech.notepad.model.Category

class CategoryAdapter(
    var categories: MutableList<Category> = mutableListOf(), val listener: CategoryItemClickListener
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryName: TextView = itemView.findViewById(R.id.category_name)
        private val editButton: ImageView = itemView.findViewById(R.id.action_edit)
        private val deleteButton: ImageView = itemView.findViewById(R.id.action_delete)

        fun setData(category: Category) {
            categoryName.text = category.categoryName

            editButton.setOnClickListener {
                listener.onEditButtonClick(category)
            }

            deleteButton.setOnClickListener {
                listener.onDeleteButtonClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(category = categories[position])
    }

    fun setCategory(lst: MutableList<Category>) {
        categories = lst
        notifyDataSetChanged()
    }

}