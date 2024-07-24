package com.lutech.notepad.ui.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lutech.notepad.R
import com.lutech.notepad.databinding.FragmentBackupBinding
import com.lutech.notepad.model.Task


class BackupFragment : Fragment(), MenuProvider {

    private var _binding: FragmentBackupBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val backupViewModel =
            ViewModelProvider(this).get(BackupViewModel::class.java)

        _binding = FragmentBackupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.text
        backupViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.addMenuProvider(this, viewLifecycleOwner)
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
        if(menuItem.itemId == R.id.menu_delete_all) {
            Toast.makeText(requireContext(), "Active", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }
}