package com.lutech.notepad.adapter

import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.lutech.notepad.R
import com.lutech.notepad.model.Task
import com.lutech.notepad.constants.*
import com.lutech.notepad.ui.add.AddActivity

class TaskAdapter(
    var tasks: List<Task> = emptyList(),
    val ctx: Context
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        private val title: TextView = itemView.findViewById(R.id.task_title)
        private val lastEdit: TextView = itemView.findViewById(R.id.task_last_edit)
        private val item: View = itemView.findViewById(R.id.note_item)

        fun setData(task: Task, context: Context) {
            title.text = task.title
            lastEdit.text = task.lastEdit
            item.setOnClickListener {
                val bundle: Bundle = Bundle()
                bundle.putInt(TASK_ID, task.id);
                bundle.putString(TASK_TITLE, task.title)
                bundle.putString(TASK_CONTENT, task.content)
                bundle.putString(TASK_LAST_EDIT, task.lastEdit)

                val it = Intent(context, AddActivity::class.java)
                it.putExtra(TASK, bundle)
                context.startActivity(it)

            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int {
                    return bindingAdapterPosition
                }

                override fun getSelectionKey(): Long? {
                    return itemId
                }

            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(tasks[position], ctx)
    }

    fun setData(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }
}