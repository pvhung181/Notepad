package com.lutech.notepad.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lutech.notepad.R
import com.lutech.notepad.adapter.TaskAdapter
import com.lutech.notepad.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: TaskAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recycler = binding.recycler
        adapter = TaskAdapter(activity = requireActivity())
        recycler.adapter = adapter
        homeViewModel.tasks.observe(viewLifecycleOwner) { adapter.setData(it) }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.setTitle(R.string.menu_notes)


        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        val appBarLayout: View? = activity?.findViewById(R.id.app_bar_main)
        val searchView = appBarLayout?.findViewById<SearchView>(R.id.search_field)

        searchView?.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    if(homeViewModel.tasks.value != null) {
                        if(newText.isNullOrBlank()) {
                            adapter.setData(homeViewModel.tasks.value!!)
                        }
                        else {
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
}