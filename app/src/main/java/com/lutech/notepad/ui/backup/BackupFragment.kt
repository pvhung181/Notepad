package com.lutech.notepad.ui.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lutech.notepad.databinding.FragmentBackupBinding


class BackupFragment : Fragment() {

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}