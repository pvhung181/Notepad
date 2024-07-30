package com.lutech.notepad.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.lutech.notepad.R
import com.lutech.notepad.adapter.TaskAdapter
import com.lutech.notepad.constants.CATEGORY_ALL
import com.lutech.notepad.constants.CATEGORY_ID
import com.lutech.notepad.constants.CATEGORY_NAME
import com.lutech.notepad.databinding.FragmentHomeBinding
import com.lutech.notepad.model.Task
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment(), MenuProvider {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var allTasks: MutableList<Task>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecycler(
            arguments?.getString(CATEGORY_NAME),
            arguments?.getInt(CATEGORY_ID)
        )


        return root
    }

    fun setupRecycler(categoryName: String?, id: Int?) {
        recycler = binding.recycler
        adapter = TaskAdapter(activity = requireActivity())
        recycler.adapter = adapter
        if(categoryName == null || categoryName == CATEGORY_ALL) {
            homeViewModel.tasks.observe(viewLifecycleOwner) {
                allTasks = it
                adapter.setData(it)
            }

        }else {
            if(id != null) {
                homeViewModel.getCategoryWithNotes(id).observe(viewLifecycleOwner){
                    if(it.isNotEmpty())  {
                        allTasks = it[0].notes.toMutableList()
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
        if(categoryName == null || categoryName == CATEGORY_ALL) {
            (activity as AppCompatActivity).supportActionBar?.subtitle = null
        }
        else {
            (activity as AppCompatActivity).supportActionBar?.subtitle = categoryName
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        val appBarLayout: View? = activity?.findViewById(R.id.app_bar_main)
        val searchView = appBarLayout?.findViewById<SearchView>(R.id.search_field)
//        val sort = appBarLayout?.findViewById<SearchView>(R.id.action_sort)
//
//        sort?.setOnClickListener {
//            showSortDialog()
//        }

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
                if(checkedItem != -1) {
                    val formatter = DateTimeFormatter.ofPattern("dd/M/yyyy hh:mm a")
                    when(checkedItem) {
                        0 -> {
                            allTasks.sortByDescending { tks -> LocalDateTime.parse(tks.lastEdit, formatter)}
                            adapter.setData(allTasks)
                        }
                        1 -> {
                            allTasks.sortBy { tks -> LocalDateTime.parse(tks.lastEdit, formatter)}
                            adapter.setData(allTasks)
                        }
                        2 -> {
                            allTasks.sortBy { tks ->tks.title}
                            adapter.setData(allTasks)
                        }
                        3 -> {
                            allTasks.sortByDescending { tks ->tks.title}
                            adapter.setData(allTasks)
                        }
                        4 -> {
                            allTasks.sortByDescending { tks -> LocalDateTime.parse(tks.createDate, formatter)}
                            adapter.setData(allTasks)

                        }
                        5 -> {

                            allTasks.sortBy { tks -> LocalDateTime.parse(tks.createDate, formatter)}
                            adapter.setData(allTasks)
                        }
                    }
                }

                dlg.dismiss()
            }
            .create()
        dialog.show()
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
        when(menuItem.itemId) {
            R.id.action_sort -> showSortDialog()
        }
        return false
    }
}