package com.lutech.notepad.ui.add

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.lutech.notepad.NotepadApplication
import com.lutech.notepad.R
import com.lutech.notepad.constants.TASK
import com.lutech.notepad.constants.TASK_CONTENT
import com.lutech.notepad.constants.TASK_ID
import com.lutech.notepad.constants.TASK_LAST_EDIT
import com.lutech.notepad.constants.TASK_TITLE
import com.lutech.notepad.database.AppDatabase
import com.lutech.notepad.database.dao.TaskDao
import com.lutech.notepad.database.repository.TaskRepository
import com.lutech.notepad.database.repository.TaskRepositoryImpl
import com.lutech.notepad.databinding.ActivityAddBinding
import com.lutech.notepad.model.Task
import com.lutech.notepad.ui.backup.BackupViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date

class AddActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding
    var isUpdate: Boolean = false
    var task: Task = Task()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val addViewModel = ViewModelProvider(this).get(AddViewModel::class.java)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isUpdate = intent.getBundleExtra(TASK) != null


        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (isUpdate) {
            val bundle = intent.getBundleExtra(TASK)
            if(bundle != null) {
                task.id = bundle.getInt(TASK_ID)
                task.title = bundle.getString(TASK_TITLE)!!
                task.content = bundle.getString(TASK_CONTENT)!!
                task.lastEdit = bundle.getString(TASK_LAST_EDIT)!!
            }

        }

        binding.titleEditText.setText(task.title)
        binding.contentEditText.setText(task.content)


        binding.save.setOnClickListener {
            task.title = binding.titleEditText.text.toString()
            task.content = binding.contentEditText.text.toString()
            task.lastEdit = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date())
            if (isUpdate) {
                addViewModel.update(task)
                Toast.makeText(this, "update successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                addViewModel.insert(task)

                Toast.makeText(this, "Add successfully", Toast.LENGTH_SHORT).show()
                clearInput()
            }

        }

    }

    private fun clearInput() {
        binding.titleEditText.text = null
        binding.contentEditText.text = null
        binding.titleEditText.requestFocus()
    }
}