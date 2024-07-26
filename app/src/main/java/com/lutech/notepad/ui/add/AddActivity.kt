package com.lutech.notepad.ui.add

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.lutech.notepad.R
import com.lutech.notepad.constants.TASK
import com.lutech.notepad.constants.TASK_CONTENT
import com.lutech.notepad.constants.TASK_DEFAULT_COLOR
import com.lutech.notepad.constants.TASK_DEFAULT_DARK_COLOR
import com.lutech.notepad.constants.TASK_ID
import com.lutech.notepad.constants.TASK_LAST_EDIT
import com.lutech.notepad.constants.TASK_TITLE
import com.lutech.notepad.data.getColors
import com.lutech.notepad.databinding.ActivityAddBinding
import com.lutech.notepad.model.Task
import com.lutech.notepad.utils.darkenColor
import com.lutech.notepad.utils.formatDate
import java.util.Date


class AddActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding
    var isUpdate: Boolean = false
    lateinit var toolbar: Toolbar
    lateinit var addViewModel: AddViewModel

    var selectedColor: String = TASK_DEFAULT_COLOR
    var darkSelectedColor: String = TASK_DEFAULT_DARK_COLOR

    var cSelectedColor: String = TASK_DEFAULT_COLOR
    var cDarkSelectedColor: String = TASK_DEFAULT_DARK_COLOR


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
            if (bundle != null) {
                task.id = bundle.getInt(TASK_ID)
                task.title = bundle.getString(TASK_TITLE)!!
                task.content = bundle.getString(TASK_CONTENT)!!
                task.lastEdit = bundle.getString(TASK_LAST_EDIT)!!
                task.color = bundle.getString(TASK_DEFAULT_COLOR)!!
                task.darkColor = bundle.getString(TASK_DEFAULT_DARK_COLOR)!!
                selectedColor = task.color
                darkSelectedColor = task.darkColor
                setCColor()
                applyColor()

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
        when (item.itemId) {
            R.id.action_save -> {
                task.title = binding.titleEditText.text.toString()
                task.content = binding.contentEditText.text.toString()
                task.lastEdit = formatDate(Date())
                task.color = selectedColor
                task.darkColor = darkSelectedColor

                if (isUpdate) {
                    addViewModel.update(task)
                    Toast.makeText(this, "update successfully", Toast.LENGTH_SHORT).show()
                } else {
                    addViewModel.insert(task)
                    Toast.makeText(this, "Add successfully", Toast.LENGTH_SHORT).show()
                    clearInput()
                    onBackPressedDispatcher.onBackPressed()
                }
            }

            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
            R.id.activity_add_action_share -> {
                if (!binding.contentEditText.text.isNullOrBlank()) {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Title : ${
                                if (binding.titleEditText.text.isNullOrBlank()) "Untitled"
                                else binding.titleEditText.text.toString()
                            } \n" + "Content : ${binding.contentEditText.text}"
                        )
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            }
            R.id.activity_add_action_colorize -> {
                showColorPickerDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clearInput() {
        binding.titleEditText.text = null
        binding.contentEditText.text = null
        binding.titleEditText.requestFocus()
    }

    private fun showColorPickerDialog() {
        val view = layoutInflater.inflate(R.layout.pick_color, null)
        val colorGrid = view.findViewById<GridLayout>(R.id.colorGrid)



        for (element in getColors()) {
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(2, 2, 2, 2)
            }

            val frame = FrameLayout(this).apply {
                layoutParams = params
            }

            val b = Button(this).apply {
                setBackgroundColor(Color.parseColor(element))
                layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }

            val plusText = TextView(this).apply {
                text = "+"
                textSize = 34f
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                elevation = 10f
                visibility = View.INVISIBLE
                layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }

            frame.addView(b)
            frame.addView(plusText)

            //Toast.makeText(this, frame.isClickable.toString(), Toast.LENGTH_SHORT).show()

            fun updateSelection() {
                for (i in 0 until colorGrid.childCount) {
                    val frame = colorGrid.getChildAt(i) as FrameLayout
                    val plusText = frame.getChildAt(1) as TextView

                        if (getColors()[i] == selectedColor) {
                            plusText.visibility = View.VISIBLE
                            darkSelectedColor = darkenColor(selectedColor)
                        }
                        else plusText.visibility = View.INVISIBLE
                }
            }

            b.setOnClickListener {
                selectedColor = element
                view.findViewById<TextView>(R.id.select_color_title).apply {
                    setBackgroundColor(Color.parseColor(element))
                }
                updateSelection()
                //Toast.makeText(this, "Active", Toast.LENGTH_SHORT).show()
            }

            view.findViewById<Button>(R.id.remove_color_button).setOnClickListener {
                selectedColor = TASK_DEFAULT_COLOR
                darkSelectedColor = TASK_DEFAULT_DARK_COLOR

                view.findViewById<TextView>(R.id.select_color_title).setBackgroundColor(getColor(R.color.transparent))

                for (i in 0 until colorGrid.childCount) {
                    val frame = colorGrid.getChildAt(i) as FrameLayout
                    val plusText = frame.getChildAt(1) as TextView
                     plusText.visibility = View.INVISIBLE
                }
            }


            colorGrid.addView(frame)
        }

        val builder = AlertDialog.Builder(this)


        val dialog = builder
            .setNegativeButton("Cancel") { dlg, _ ->
                resetCurrentColor()
                //todo
//                selectedColor = TASK_DEFAULT_COLOR
//                darkSelectedColor = TASK_DEFAULT_DARK_COLOR
                dlg.dismiss() }
            .setPositiveButton("OK") { dlg, _ ->
                applyColor()
                setCColor()
                task.color = selectedColor
                task.color = darkSelectedColor
                dlg.dismiss()
            } //Todo
            .setView(view)
            .create()
        dialog.show()

    }

    private fun resetDefaultColor() {
        selectedColor = TASK_DEFAULT_COLOR
        darkSelectedColor = TASK_DEFAULT_DARK_COLOR
        setTextLayoutBackground(selectedColor)
        binding.toolbar.setBackgroundColor(Color.parseColor(darkSelectedColor))
    }

    private fun resetCurrentColor() {
        selectedColor = cSelectedColor
        darkSelectedColor = cDarkSelectedColor
    }

    private fun applyColor() {
        setTextLayoutBackground(selectedColor)
        binding.toolbar.setBackgroundColor(Color.parseColor(darkSelectedColor))
        binding.addLayout.setBackgroundColor(Color.parseColor(darkSelectedColor))
    }

    private fun setTextLayoutBackground(color: String) {
        val background = binding.textLayout.background as GradientDrawable
        background.setColor(Color.parseColor(color))
        binding.textLayout.background = background

    }

    private fun setCColor() {
        cSelectedColor = selectedColor
        cDarkSelectedColor = darkSelectedColor
    }

}