package com.lutech.notepad.ui.add

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.lutech.notepad.R
import com.lutech.notepad.constants.TASK
import com.lutech.notepad.constants.TASK_CONTENT
import com.lutech.notepad.constants.TASK_ID
import com.lutech.notepad.constants.TASK_LAST_EDIT
import com.lutech.notepad.constants.TASK_TITLE
import com.lutech.notepad.databinding.ActivityAddBinding
import com.lutech.notepad.model.Task
import com.lutech.notepad.utils.formatDate
import java.util.Date

class AddActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding
    var isUpdate: Boolean = false
    lateinit var toolbar: Toolbar
    lateinit var addViewModel: AddViewModel
    var task: Task = Task()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addViewModel = ViewModelProvider(this).get(AddViewModel::class.java)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isUpdate = intent.getBundleExtra(TASK) != null
        toolbar = binding.toolbar




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

        binding.titleEditText.requestFocus()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_add_option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_save) {
            task.title = binding.titleEditText.text.toString()
            task.content = binding.contentEditText.text.toString()
            task.lastEdit = formatDate(Date())
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

        else if(item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun clearInput() {
        binding.titleEditText.text = null
        binding.contentEditText.text = null
        binding.titleEditText.requestFocus()
    }
}