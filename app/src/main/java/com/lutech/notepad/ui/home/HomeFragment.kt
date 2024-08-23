package com.lutech.notepad.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.lutech.notepad.R
import com.lutech.notepad.adapter.TaskAdapter
import com.lutech.notepad.constants.APP_SHARED_PREFERENCES
import com.lutech.notepad.constants.CATEGORY_ALL
import com.lutech.notepad.constants.CATEGORY_ID
import com.lutech.notepad.constants.CATEGORY_NAME
import com.lutech.notepad.constants.TASK
import com.lutech.notepad.constants.TASK_CONTENT
import com.lutech.notepad.constants.TASK_CREATION_DATE
import com.lutech.notepad.constants.TASK_DEFAULT_COLOR
import com.lutech.notepad.constants.TASK_DEFAULT_DARK_COLOR
import com.lutech.notepad.constants.TASK_ID
import com.lutech.notepad.constants.TASK_LAST_EDIT
import com.lutech.notepad.constants.TASK_TITLE
import com.lutech.notepad.databinding.FragmentHomeBinding
import com.lutech.notepad.listener.NoteItemClickListener
import com.lutech.notepad.model.Task
import com.lutech.notepad.ui.TaskViewModel
import com.lutech.notepad.ui.add.AddActivity
import com.lutech.notepad.utils.ApplicationPreferenceManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment(), MenuProvider, NoteItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mainViewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var allTasks: MutableList<Task>
    private lateinit var applicationPreferenceManager: ApplicationPreferenceManager

    private val callback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater = mode?.menuInflater

            inflater?.inflate(R.menu.select_menu, menu)
            menu?.findItem(R.id.menu_delete)?.icon?.setTint(requireActivity().getColor(R.color.white))
            menu?.findItem(R.id.menu_select_all)?.icon?.setTint(requireActivity().getColor(R.color.white))

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            adapter.isEnable = true
            mainViewModel.getText().observe(this@HomeFragment ) { value ->
                mode?.title = String.format("%s Selected", value)
            }
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item!!.itemId) {
                R.id.menu_delete -> {
                    android.app.AlertDialog.Builder(activity)
                        .setMessage("Delete the selected notes?")
                        .setNegativeButton("Cancel") { dlg, _ -> dlg.dismiss() }
                        .setPositiveButton("OK") { dlg, _ ->
                            Toast.makeText(
                                activity,
                                "Deleted notes (${adapter.selectList.size})",
                                Toast.LENGTH_SHORT
                            ).show()
                            for (s in adapter.selectList) {
                                adapter.tasks.remove(s)
                                //update is_deleted = true not delete
                                mainViewModel.moveToTrash(s)

                            }
                            mode?.finish()
                            dlg.dismiss()
                        }
                        .create().show()


                }

                R.id.menu_select_all -> {
                    adapter.toggleSelectAll()
                    mainViewModel.setText(adapter.selectList.size.toString())
                    adapter.notifyDataSetChanged()
                }
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            adapter.destroySelectedList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()
        setupRecycler(
            arguments?.getString(CATEGORY_NAME),
            arguments?.getInt(CATEGORY_ID)
        )

        return root
    }

    private fun init() {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        mainViewModel =
            ViewModelProvider(this)[TaskViewModel::class.java]
        applicationPreferenceManager =
            ApplicationPreferenceManager(APP_SHARED_PREFERENCES, requireContext())
    }

    private fun setupIfIsFirstTime() {
        if (applicationPreferenceManager.isFirstTime()) {
            binding.textviewInstruction.visibility = View.VISIBLE
            binding.iconBottomRight.visibility = View.VISIBLE

            homeViewModel.insertTask(
                Task(
                    title = getString(R.string.task_title_instruction),
                    content = getString(R.string.task_content_instruction)
                )
            )
        } else {
            binding.textviewInstruction.visibility = View.GONE
            binding.iconBottomRight.visibility = View.GONE

        }
    }

    private fun setupRecycler(categoryName: String?, id: Int?) {
        recycler = binding.recycler
        adapter = TaskAdapter(listener = this)
        recycler.adapter = adapter
        if (categoryName == null || categoryName == CATEGORY_ALL) {
            homeViewModel.tasks.observe(viewLifecycleOwner) {
                allTasks = it
                adapter.setData(it)
            }

        } else {
            if (id != null) {
                homeViewModel.getCategoryWithNotes(id).observe(viewLifecycleOwner) {
                    if (it.isNotEmpty()) {
                        allTasks = it[0].notes.filter { task -> !task.isDeleted }.toMutableList()
                        adapter.setData(allTasks)
                    }
                }
            }

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.addMenuProvider(this, viewLifecycleOwner)
        activity?.setTitle(R.string.menu_notes)
        val categoryName = arguments?.getString(CATEGORY_NAME)
        if (categoryName == null || categoryName == CATEGORY_ALL) {
            (activity as AppCompatActivity).supportActionBar?.subtitle = null
        } else {
            (activity as AppCompatActivity).supportActionBar?.subtitle = categoryName
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        val appBarLayout: View? = activity?.findViewById(R.id.app_bar_main)
        val searchView = appBarLayout?.findViewById<SearchView>(R.id.search_field)

        setupIfIsFirstTime()

        searchView?.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (homeViewModel.tasks.value != null) {
                        if (newText.isNullOrBlank()) {
                            adapter.setData(homeViewModel.tasks.value!!)
                        } else {
                            adapter.setData(
                                homeViewModel.tasks.value!!.filter {
                                    it.title.contains(newText)
                                }.toMutableList()
                            )
                        }
                    }
                    return true
                }

            }
        )
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.main, menu)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_sort -> showSortDialog()
            R.id.activity_main_action_select_all -> {
                requireActivity().startActionMode(callback)
                adapter.toggleSelectAll()
                mainViewModel.setText(adapter.selectList.size.toString())
                adapter.notifyDataSetChanged()
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showSortDialog() {
        val builder = AlertDialog.Builder(requireContext())
        var checkedItem = -1
        builder.setTitle("Sort")
        val listItems = arrayOf(
            "edit date: from newest",
            "edit date: from oldest",
            "title: A to Z",
            "title: Z to A",
            "creation date: from newest",
            "creation date: from oldest"
        )

        val dialog = builder.setSingleChoiceItems(listItems, checkedItem) { dlg, which ->
            checkedItem = which
        }.setNegativeButton("Cancel") { dlg, _ -> dlg.dismiss() }
            .setPositiveButton("Sort") { dlg, _ ->
                if (checkedItem != -1) {
                    val formatter = DateTimeFormatter.ofPattern("dd/M/yyyy hh:mm a")
                    when (checkedItem) {
                        0 -> {
                            allTasks.sortByDescending { tks ->
                                LocalDateTime.parse(
                                    tks.lastEdit,
                                    formatter
                                )
                            }
                            adapter.setData(allTasks)
                        }

                        1 -> {
                            allTasks.sortBy { tks -> LocalDateTime.parse(tks.lastEdit, formatter) }
                            adapter.setData(allTasks)
                        }

                        2 -> {
                            allTasks.sortBy { tks -> tks.title }
                            adapter.setData(allTasks)
                        }

                        3 -> {
                            allTasks.sortByDescending { tks -> tks.title }
                            adapter.setData(allTasks)
                        }

                        4 -> {
                            allTasks.sortByDescending { tks ->
                                LocalDateTime.parse(
                                    tks.createDate,
                                    formatter
                                )
                            }
                            adapter.setData(allTasks)

                        }

                        5 -> {

                            allTasks.sortBy { tks ->
                                LocalDateTime.parse(
                                    tks.createDate,
                                    formatter
                                )
                            }
                            adapter.setData(allTasks)
                        }
                    }
                }

                dlg.dismiss()
            }
            .create()
        dialog.show()
    }

    override fun setOnLongClickListener() {
        requireActivity().startActionMode(callback)
    }

    override fun setOnClickListener(task: Task) {
        val bundle = Bundle()
        bundle.putInt(TASK_ID, task.taskId);
        bundle.putString(TASK_TITLE, task.title)
        bundle.putString(TASK_CONTENT, task.content)
        bundle.putString(TASK_LAST_EDIT, task.lastEdit)
        bundle.putString(TASK_CREATION_DATE, task.createDate)
        bundle.putString(TASK_DEFAULT_COLOR, task.color)
        bundle.putString(TASK_DEFAULT_DARK_COLOR, task.darkColor)

        val it = Intent(requireActivity(), AddActivity::class.java)
        it.putExtra(TASK, bundle)
        requireActivity().startActivity(it)
    }

    override fun setOnClickInSelectedMode() {
        mainViewModel.setText(adapter.selectList.size.toString())
    }
}