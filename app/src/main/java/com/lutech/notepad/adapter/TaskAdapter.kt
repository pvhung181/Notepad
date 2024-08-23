package com.lutech.notepad.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Html
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lutech.notepad.R.id
import com.lutech.notepad.R.layout
import com.lutech.notepad.listener.NoteItemClickListener
import com.lutech.notepad.model.Task


class TaskAdapter(
    var tasks: MutableList<Task> = mutableListOf(),
    val listener: NoteItemClickListener
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    var isEnable = false
    var isSelectAll = false
    var selectList = mutableListOf<Task>()


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(id.task_title)
        private val lastEdit: TextView = itemView.findViewById(id.task_last_edit)
        val item: View = itemView.findViewById(id.note_item)

        //val background: View = itemView.findViewById(id.task_background)
        val overlay: View = itemView.findViewById(id.view_overlay)

        fun setData(task: Task) {
            if (task.title.isBlank()) {
                if (task.content.isNotEmpty()) {
                    title.text = SpannableString(
                        Html.fromHtml(
                            task.content,
                            Html.FROM_HTML_MODE_LEGACY
                        )
                    ).toString()
                } else {
                    title.text = "Untitled"
                }
            } else {
                title.text = task.title
            }
            lastEdit.text = task.lastEdit

            val background = item.background as GradientDrawable
            background.setColor(Color.parseColor(task.color))
            item.background = background
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(layout.task_item, parent, false)

        return ViewHolder(view)
    }


    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(tasks[position])
        holder.item.setOnClickListener {
            if (isEnable) {
                clickItem(holder);
            } else {
                listener.setOnClickListener(tasks[position])
            }
        }

        holder.item.setOnLongClickListener {
            if (!isEnable) {
                clickItem(holder)
                listener.setOnLongClickListener()

            } else {
                clickItem(holder)
            }
            true
        }


        holder.overlay.visibility = if (isSelectAll) View.VISIBLE else View.INVISIBLE
    }

    fun setData(tasks: MutableList<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }


    private fun clickItem(holder: ViewHolder) {

        val s = tasks[holder.absoluteAdapterPosition]

        if (holder.overlay.visibility == View.INVISIBLE) {
            holder.overlay.visibility = View.VISIBLE
            selectList.add(s)
        } else {
            holder.overlay.visibility = View.INVISIBLE
            selectList.remove(s)
        }

        listener.setOnClickInSelectedMode()
    }

    fun toggleSelectAll() {
        if (selectList.size == tasks.size) {
            isSelectAll = false
            selectList.clear()
        } else {
            isSelectAll = true
            selectList.clear()
            selectList.addAll(tasks)
        }
        notifyDataSetChanged()
    }

    fun destroySelectedList() {
        isEnable = false
        isSelectAll = false
        selectList.clear()
        notifyDataSetChanged()
    }

    fun deleteSelectedTasks() {
        tasks.removeAll(selectList)
        selectList.clear()
        isEnable = false
        isSelectAll = false
        notifyDataSetChanged()
    }
}



