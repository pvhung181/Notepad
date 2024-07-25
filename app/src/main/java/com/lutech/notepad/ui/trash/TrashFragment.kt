package com.lutech.notepad.ui.trash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lutech.notepad.R
import com.lutech.notepad.adapter.TaskAdapter
import com.lutech.notepad.adapter.TaskDeletedAdapter
import com.lutech.notepad.databinding.FragmentTrashBinding

class TrashFragment : Fragment(), MenuProvider {

    private var _binding: FragmentTrashBinding? = null

    private val binding get() = _binding!!

    private lateinit var trashViewModel: TrashViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        trashViewModel = ViewModelProvider(this)[TrashViewModel::class.java]

        _binding = FragmentTrashBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recycler = binding.recycler
        val adapter = TaskDeletedAdapter(activity = requireActivity())
        recycler.adapter = adapter
        trashViewModel.tasks.observe(viewLifecycleOwner) { adapter.setData(it) }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.addMenuProvider(this, viewLifecycleOwner)
        activity?.setTitle(R.string.trash)
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
        when(menuItem.itemId) {
            R.id.menu_undelete_all -> {
                trashViewModel.restoreAll()
            }
            R.id.menu_export -> {

            }
            R.id.menu_empty_trash -> {
                trashViewModel.deleteAll()
            }
        }
        return false
    }

}