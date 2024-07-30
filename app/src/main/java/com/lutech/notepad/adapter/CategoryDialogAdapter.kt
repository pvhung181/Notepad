package com.lutech.notepad.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.lutech.notepad.R
import com.lutech.notepad.model.Category
import com.lutech.notepad.model.CategoryTaskCrossRef
import com.lutech.notepad.model.Task
import com.lutech.notepad.ui.TaskViewModel

class CategoryDialogAdapter(
    var categories: MutableList<Category> = mutableListOf(),
    var checkedCategory: MutableList<Category> = mutableListOf(),
    val task: Task,
    val activity: Activity
) : RecyclerView.Adapter<CategoryDialogAdapter.ViewHolder>() {

    private lateinit var viewModel: TaskViewModel

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryName: TextView = itemView.findViewById(R.id.dialog_category_name)
        private val checkbox: CheckBox = itemView.findViewById(R.id.category_checkbox)



        fun setData(category: Category, isChecked: Boolean) {
            categoryName.text = category.categoryName

            if(isChecked) checkbox.isChecked = true

            checkbox.setOnClickListener {
                if(checkbox.isChecked) viewModel.insertCategoryNote(CategoryTaskCrossRef(category.categoryId, task.taskId))
                else viewModel.deleteCategoryNote(CategoryTaskCrossRef(category.categoryId, task.taskId))
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item_checkbox, parent, false)

        viewModel = ViewModelProvider(activity as FragmentActivity)[TaskViewModel::class]

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = categories.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        for(i in checkedCategory)
            if(i.categoryId == categories[position].categoryId) {
                holder.setData(category = categories[position], isChecked = true)
                return
            }
        holder.setData(category = categories[position], isChecked = false)

    }

    fun setCategory(lst: MutableList<Category>) {
        categories = lst
        notifyDataSetChanged()
    }

    fun setCheckCategory(lst: MutableList<Category>) {
        checkedCategory = lst
        notifyDataSetChanged()
    }

}