package com.lutech.notepad.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.lutech.notepad.R
import com.lutech.notepad.R.id
import com.lutech.notepad.R.layout
import com.lutech.notepad.constants.TASK
import com.lutech.notepad.constants.TASK_CONTENT
import com.lutech.notepad.constants.TASK_ID
import com.lutech.notepad.constants.TASK_LAST_EDIT
import com.lutech.notepad.constants.TASK_TITLE
import com.lutech.notepad.model.Task
import com.lutech.notepad.ui.add.AddActivity
import com.lutech.notepad.ui.home.HomeViewModel


class TaskAdapter(
    var tasks: MutableList<Task> = mutableListOf(),
    val activity: Activity
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    var mainViewModel: HomeViewModel? = null
    var isEnable = false
    var isSelectAll = false
    var selectList = mutableListOf<Task>()


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        private val title: TextView = itemView.findViewById(id.task_title)
        private val lastEdit: TextView = itemView.findViewById(id.task_last_edit)
        val item: View = itemView.findViewById(id.note_item)
        val overlay: View = itemView.findViewById(id.view_overlay)

        fun setData(task: Task) {
            title.text = task.title
            lastEdit.text = task.lastEdit
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(layout.task_item, parent, false)

        mainViewModel =
            ViewModelProvider(activity as FragmentActivity)[HomeViewModel::class.java]


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
                        inflater?.inflate(R.menu.select_menu, menu)
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
                            id.menu_delete -> {
                                for (s in selectList) {
                                    tasks.remove(s)
                                    mainViewModel?.deleteTask(s)
                                }

                                mode?.finish()
                            }

                            id.menu_select_all -> {
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
                val bundle = Bundle()
                bundle.putInt(TASK_ID, tasks[position].id);
                bundle.putString(TASK_TITLE, tasks[position].title)
                bundle.putString(TASK_CONTENT, tasks[position].content)
                bundle.putString(TASK_LAST_EDIT, tasks[position].lastEdit)

                val it = Intent(activity, AddActivity::class.java)
                it.putExtra(TASK, bundle)
                activity.startActivity(it)
            }
        }

        holder.overlay.visibility = if(isSelectAll) View.VISIBLE else View.INVISIBLE
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



