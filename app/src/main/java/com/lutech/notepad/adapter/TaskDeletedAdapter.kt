package com.lutech.notepad.adapter

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.lutech.notepad.R
import com.lutech.notepad.R.id
import com.lutech.notepad.R.layout
import com.lutech.notepad.model.Task
import com.lutech.notepad.ui.TaskViewModel


class TaskDeletedAdapter(
    var tasks: MutableList<Task> = mutableListOf(), val activity: Activity
) : RecyclerView.Adapter<TaskDeletedAdapter.ViewHolder>() {
    var mainViewModel: TaskViewModel? = null
    var isEnable = false
    var isSelectAll = false
    var selectList = mutableListOf<Task>()


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        private val title: TextView = itemView.findViewById(id.task_title)
        private val lastEdit: TextView = itemView.findViewById(id.task_last_edit)
        val item: View = itemView.findViewById(id.note_item)
        val overlay: View = itemView.findViewById(id.view_overlay)

        fun setData(task: Task) {
            title.text = if (task.title == "") "Untitled" else task.title
            lastEdit.text = task.lastEdit

            val background = item.background as GradientDrawable
            background.setColor(Color.parseColor(task.color))
            item.background = background

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout.task_item, parent, false)

        mainViewModel = ViewModelProvider(activity as FragmentActivity)[TaskViewModel::class.java]


        return ViewHolder(view)
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(tasks[position])
        holder.item.setOnLongClickListener {
            if (!isEnable) {
                val callback: ActionMode.Callback2 = object : ActionMode.Callback2() {
                    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                        val inflater = mode?.menuInflater
                        inflater?.inflate(R.menu.trash_multi_select_menu, menu)
                        menu?.findItem(R.id.trash_restore)?.icon?.setTint(activity.getColor(R.color.white))
                        menu?.findItem(R.id.trash_select_all)?.icon?.setTint(activity.getColor(R.color.white))
                        return true
                    }

                    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                        isEnable = true
                        clickItem(holder)
                        mainViewModel?.getText()?.observe(
                            activity as LifecycleOwner
                        ) { value -> mode?.title = String.format("%s Selected", value) }
                        return true
                    }

                    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                        when (item!!.itemId) {
                            id.trash_restore -> {
                                for (s in selectList) {
                                    tasks.remove(s)
                                    mainViewModel?.restoreTask(s)
                                }

                                mode?.finish()
                            }

                            id.trash_select_all -> {
                                if (selectList.size == tasks.size) {
                                    isSelectAll = false
                                    selectList.clear()
                                } else {
                                    isSelectAll = true
                                    selectList.clear()
                                    selectList.addAll(tasks)
                                }
                                mainViewModel?.setText(selectList.size.toString())
                                notifyDataSetChanged()
                            }
                        }
                        return true
                    }

                    override fun onDestroyActionMode(mode: ActionMode?) {

                        isEnable = false
                        isSelectAll = false



                        selectList.clear()

                        notifyDataSetChanged()
                    }
                }
                activity.startActionMode(callback)
            } else {
                clickItem(holder)
            }
            true
        }
        holder.item.setOnClickListener {
            if (isEnable) {
                clickItem(holder);
            } else {
                val builder = AlertDialog.Builder(activity)
                var checkedItem = 0
                builder.setTitle("Select an action for the note")
                val listItems = arrayOf(
                    "Restore",
                    "Permanently delete",
                )

                val dialog = builder.setSingleChoiceItems(listItems, checkedItem) { dlg, which -> checkedItem = which
                }
                    .setNegativeButton("Cancel") { dlg, _ -> dlg.dismiss() }
                    .setPositiveButton("OK") { dlg, _ ->
                        if (checkedItem == 0) {
                            mainViewModel?.restoreTask(tasks[position])
                        } else {
                            mainViewModel?.deleteTask(tasks[position])
                        }
                        dlg?.dismiss()
                    }.create()
                dialog.show()
            }
        }

        holder.overlay.visibility = if (isSelectAll) View.VISIBLE else View.INVISIBLE
    }

    fun setData(tasks: MutableList<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    private fun clickItem(holder: ViewHolder): Unit {

        val s = tasks[holder.absoluteAdapterPosition]

        if (holder.overlay.visibility == View.INVISIBLE) {
            holder.overlay.visibility = View.VISIBLE
            selectList.add(s)
        } else {
            holder.overlay.visibility = View.INVISIBLE
            selectList.remove(s)
        }

        mainViewModel?.setText(selectList.size.toString())


    }
}



