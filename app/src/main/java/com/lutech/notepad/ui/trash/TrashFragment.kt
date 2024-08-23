package com.lutech.notepad.ui.trash

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.lutech.notepad.R
import com.lutech.notepad.adapter.TaskAdapter
import com.lutech.notepad.databinding.FragmentTrashBinding
import com.lutech.notepad.listener.NoteItemClickListener
import com.lutech.notepad.model.Task
import com.lutech.notepad.ui.TaskViewModel

class TrashFragment : Fragment(), MenuProvider, NoteItemClickListener {

    private var _binding: FragmentTrashBinding? = null
    private val binding get() = _binding!!

    private lateinit var trashViewModel: TrashViewModel
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrashBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()

        val recycler = binding.recycler
        adapter = TaskAdapter(listener = this)
        recycler.adapter = adapter

        trashViewModel.tasks.observe(viewLifecycleOwner) { adapter.setData(it) }

        return root
    }

    private fun init() {
        trashViewModel = ViewModelProvider(this)[TrashViewModel::class.java]
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.addMenuProvider(this, viewLifecycleOwner)
        activity?.setTitle(R.string.trash)
        (activity as AppCompatActivity).supportActionBar?.subtitle = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.trash_fragment_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_undelete_all -> {
                showUndeleteAllDialog()
            }

            R.id.menu_export -> {

            }

            R.id.menu_empty_trash -> {
                showEmptyTrashDialog()
            }
        }
        return false
    }

    private fun showUndeleteAllDialog() {
        val builder = AlertDialog.Builder(requireActivity())

        val dialog = builder
            .setMessage("Restore all notes?")
            .setNegativeButton("NO") { dlg, _ -> dlg.dismiss() }
            .setPositiveButton("YES") { dlg, _ ->
                trashViewModel.restoreAll()
                dlg.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun showEmptyTrashDialog() {
        val builder = AlertDialog.Builder(requireActivity())

        val dialog = builder
            .setMessage("All trashed notes will be deleted permanently. Are you sure that you want to delete all od the trashed notes??")
            .setNegativeButton("NO") { dlg, _ -> dlg.dismiss() }
            .setPositiveButton("YES") { dlg, _ ->
                trashViewModel.deleteAll()
                dlg.dismiss()
            }
            .create()
        dialog.show()
    }

    override fun setOnLongClickListener() {
        val callback: ActionMode.Callback2 = object : ActionMode.Callback2() {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                val inflater = mode?.menuInflater
                inflater?.inflate(R.menu.trash_multi_select_menu, menu)
                menu?.findItem(R.id.trash_restore)?.icon?.setTint(requireActivity().getColor(R.color.white))
                menu?.findItem(R.id.trash_select_all)?.icon?.setTint(requireActivity().getColor(R.color.white))
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                adapter.isEnable = true
                taskViewModel.getText().observe(
                    activity as LifecycleOwner
                ) { value -> mode?.title = String.format("%s Selected", value) }
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                when (item!!.itemId) {
                    R.id.trash_restore -> {
                        android.app.AlertDialog.Builder(activity)
                            .setMessage("Restore the selected notes?")
                            .setNegativeButton("Cancel") { dlg, _ -> dlg.dismiss() }
                            .setPositiveButton("OK") { dlg, _ ->
                                for (s in adapter.selectList) {
                                    adapter.tasks.remove(s)
                                    taskViewModel.restoreTask(s)
                                }

                                mode?.finish()
                                dlg.dismiss()
                            }
                            .create().show()
                    }

                    R.id.trash_select_all -> {
                        adapter.toggleSelectAll()
                        taskViewModel.setText(adapter.selectList.size.toString())
                        adapter.notifyDataSetChanged()
                    }

                    R.id.trash_delete -> {
                        android.app.AlertDialog.Builder(activity)
                            .setMessage("Are you sure that you want to delete the selected notes ? The notes will be deleted permanently")
                            .setNegativeButton("Cancel") { dlg, _ -> dlg.dismiss() }
                            .setPositiveButton("OK") { dlg, _ ->
                                Toast.makeText(
                                    activity,
                                    "Deleted notes (${adapter.selectList.size})",
                                    Toast.LENGTH_SHORT
                                ).show()
                                for (s in adapter.selectList) {
                                    adapter.tasks.remove(s)
                                    taskViewModel.deleteTask(s)
                                }

                                mode?.finish()
                                dlg.dismiss()
                            }
                            .create().show()
                    }
                }
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                adapter.destroySelectedList()
            }
        }
        requireActivity().startActionMode(callback)
    }

    override fun setOnClickListener(task: Task) {
        val builder = AlertDialog.Builder(requireActivity())
        var checkedItem = 0
        builder.setTitle("Select an action for the note")
        val listItems = arrayOf(
            "Restore",
            "Permanently delete",
        )

        val dialog = builder.setSingleChoiceItems(listItems, checkedItem) { dlg, which ->
            checkedItem = which
        }
            .setNegativeButton("Cancel") { dlg, _ -> dlg.dismiss() }
            .setPositiveButton("OK") { dlg, _ ->
                if (checkedItem == 0) {
                    taskViewModel.restoreTask(task)
                } else {
                    taskViewModel.deleteTask(task)
                }
                dlg?.dismiss()
            }.create()
        dialog.show()
    }

    override fun setOnClickInSelectedMode() {
        taskViewModel.setText(adapter.selectList.size.toString())
    }

}