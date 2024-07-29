package com.lutech.notepad.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.lutech.notepad.R
import com.lutech.notepad.model.Category
import com.lutech.notepad.ui.TaskViewModel

class CategoryAdapter(
    var categories: MutableList<Category> = mutableListOf(),
    val activity: Activity
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private lateinit var viewModel: TaskViewModel

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryName: TextView = itemView.findViewById(R.id.category_name)
        private val editButton: ImageView = itemView.findViewById(R.id.action_edit)
        private val deleteButton: ImageView = itemView.findViewById(R.id.action_delete)



        fun setData(category: Category) {
            categoryName.text = category.categoryName

            editButton.setOnClickListener {
                showEditDialog(category)
            }

            deleteButton.setOnClickListener {
                viewModel.deleteCategory(category)
            }

        }

    }

    fun showEditDialog(c: Category) {
        val builder = AlertDialog.Builder(activity)
        val editText = EditText(activity)

        editText.setText(c.categoryName)

        val dialog = builder
            .setTitle("Edit category name")
            .setView(editText)
            .setNegativeButton("Cancel") { dlg, _ -> dlg.dismiss() }
            .setPositiveButton("Update") { dlg, _ ->
                viewModel.updateCategory(c.copy(categoryName = editText.text.toString()))
                dlg.dismiss()
            }
            .create()
        dialog.show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)

        viewModel = ViewModelProvider(activity as FragmentActivity)[TaskViewModel::class]

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