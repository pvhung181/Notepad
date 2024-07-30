package com.lutech.notepad.ui.add

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog.show
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.lutech.notepad.R
import com.lutech.notepad.adapter.CategoryDialogAdapter
import com.lutech.notepad.constants.TASK
import com.lutech.notepad.constants.TASK_CONTENT
import com.lutech.notepad.constants.TASK_CREATION_DATE
import com.lutech.notepad.constants.TASK_DEFAULT_COLOR
import com.lutech.notepad.constants.TASK_DEFAULT_DARK_COLOR
import com.lutech.notepad.constants.TASK_ID
import com.lutech.notepad.constants.TASK_LAST_EDIT
import com.lutech.notepad.constants.TASK_TITLE
import com.lutech.notepad.data.getColors
import com.lutech.notepad.databinding.ActivityAddBinding
import com.lutech.notepad.model.Task
import com.lutech.notepad.ui.TaskViewModel
import com.lutech.notepad.utils.countWord
import com.lutech.notepad.utils.darkenColor
import com.lutech.notepad.utils.formatDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
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

    lateinit var taskViewModel: TaskViewModel



    var task: Task = Task()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addViewModel = ViewModelProvider(this).get(AddViewModel::class.java)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupFormatBarListeners()
        isUpdate = intent.getBundleExtra(TASK) != null
        toolbar = binding.toolbar
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        val bundle = intent.getBundleExtra(TASK)
        if (bundle != null) {
            task.taskId = bundle.getInt(TASK_ID)
            task.title = bundle.getString(TASK_TITLE)!!
            task.content = bundle.getString(TASK_CONTENT)!!
            task.lastEdit = bundle.getString(TASK_LAST_EDIT)!!
            task.createDate = bundle.getString(TASK_CREATION_DATE)!!
            task.color = bundle.getString(TASK_DEFAULT_COLOR)!!
            task.darkColor = bundle.getString(TASK_DEFAULT_DARK_COLOR)!!
            selectedColor = task.color
            darkSelectedColor = task.darkColor
            setCColor()
            applyColor()

        }

        binding.titleEditText.setText(task.title)
        binding.contentEditText.setText(task.content)

        binding.cancelBtn.setOnClickListener {
            binding.formatBar.visibility = View.GONE
        }

        binding.titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTask(task.copy(title = s.toString()))
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.contentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTask(task.copy(content = s.toString()))
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.titleEditText.requestFocus()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun setupFormatBarListeners() {
        binding.boldBtn.setOnClickListener {
            if(binding.boldBtn.foreground == null) {
                binding.boldBtn.apply {
                    foreground =  ColorDrawable(ContextCompat.getColor(this@AddActivity, R.color.black))
                    alpha = 0.1f
                }
            }
            else {
                binding.boldBtn.foreground = null
                binding.boldBtn.alpha = 1f
            }
        }

        binding.italicBtn.setOnClickListener {
            if(binding.italicBtn.foreground == null) {
                binding.italicBtn.apply {
                    foreground =  ColorDrawable(ContextCompat.getColor(this@AddActivity, R.color.black))
                    alpha = 0.1f
                }
            }
            else {
                binding.italicBtn.foreground = null
                binding.italicBtn.alpha = 1f
            }
        }
        
        binding.underlinedBtn.setOnClickListener {
            if(binding.underlinedBtn.foreground == null) {
                binding.underlinedBtn.apply {
                    foreground =  ColorDrawable(ContextCompat.getColor(this@AddActivity, R.color.black))
                    alpha = 0.1f
                }
            }
            else {
                binding.underlinedBtn.foreground = null
                binding.underlinedBtn.alpha = 1f
            }
        }

        binding.strikethroughBtn.setOnClickListener {
            if(binding.strikethroughBtn.foreground == null) {
                binding.strikethroughBtn.apply {
                    foreground =  ColorDrawable(ContextCompat.getColor(this@AddActivity, R.color.black))
                    alpha = 0.1f
                }
            }
            else {
                binding.strikethroughBtn.foreground = null
                binding.strikethroughBtn.alpha = 1f
            }
        }

        binding.colorFillBtn.setOnClickListener {
            if(binding.colorFillBtn.foreground == null) {
                binding.colorFillBtn.apply {
                    foreground =  ColorDrawable(ContextCompat.getColor(this@AddActivity, R.color.black))
                    alpha = 0.1f
                }
            }
            else {
                binding.colorFillBtn.foreground = null
                binding.colorFillBtn.alpha = 1f
            }
        }

        binding.colorTextBtn.setOnClickListener {
            if(binding.colorTextBtn.foreground == null) {
                binding.colorTextBtn.apply {
                    foreground =  ColorDrawable(ContextCompat.getColor(this@AddActivity, R.color.black))
                    alpha = 0.1f
                }
            }
            else {
                binding.colorTextBtn.foreground = null
                binding.colorTextBtn.alpha = 1f
            }
        }

        binding.formatSizeBtn.setOnClickListener {
            if(binding.formatSizeBtn.foreground == null) {
                binding.formatSizeBtn.apply {
                    foreground =  ColorDrawable(ContextCompat.getColor(this@AddActivity, R.color.black))
                    alpha = 0.1f
                }
            }
            else {
                binding.formatSizeBtn.foreground = null
                binding.formatSizeBtn.alpha = 1f
            }
        }




    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_add_option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                updateTask(
                    task.copy(
                        title = binding.titleEditText.text.toString(),
                        content = binding.contentEditText.text.toString(),
                        lastEdit = formatDate(Date()),
                        color = selectedColor,
                        darkColor = darkSelectedColor
                    )
                )

                addViewModel.update(task)
                Toast.makeText(this, "update successfully", Toast.LENGTH_SHORT).show()
            }

            R.id.activity_add_action_delete -> {
                val categoryBuilder = AlertDialog.Builder(this)
                categoryBuilder.setMessage("The ${task.title} note will be deleted. Are you sure ?")
                categoryBuilder.setPositiveButton("Delete") { dialog, which ->
                    taskViewModel.deleteTask(task)
                    onBackPressedDispatcher.onBackPressed()
                }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss()}
                categoryBuilder.create().show()
            }

            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()


            }

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

            R.id.activity_add_action_read_mode -> {
                //todo
                binding.titleEditText.isEnabled = false
                binding.contentEditText.isEnabled = false
                binding.contentEditText.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.titleEditText.setTextColor(ContextCompat.getColor(this, R.color.black))

//                val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
//                    override fun onDoubleTap(e: MotionEvent): Boolean {
//                        Toast.makeText(this@AddActivity, "Double tap", Toast.LENGTH_LONG).show()
//                        return true
//                    }
//
//                    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
//                        Toast.makeText(this@AddActivity, "Tap", Toast.LENGTH_LONG).show()
//                        return true
//                    }
//                })

                //binding.textLayout.setOnTouchListener { v, event -> gestureDetector.onTouchEvent(event)  }


                //editText.setHintTextColor(ContextCompat.getColor(context, R.color.normal_hint_color))
            }

            R.id.activity_add_action_categorize -> {
                createCategorizeDialog()
            }

            R.id.activity_add_action_show_info -> {
                showInformationDialog()
            }

            R.id.activity_add_action_export -> {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TITLE, "${title}.txt")
                }
                createDocumentLauncher.launch(intent)
            }

            R.id.activity_add_action_format_bar -> {
                if (binding.formatBar.visibility == View.GONE) binding.formatBar.visibility =
                    View.VISIBLE
                else binding.formatBar.visibility = View.GONE
            }
        }
        return super.onOptionsItemSelected(item)
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
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val plusText = TextView(this).apply {
                text = "+"
                textSize = 34f
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                elevation = 10f
                visibility = View.INVISIBLE
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
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
                    } else plusText.visibility = View.INVISIBLE
                }
            }

            b.setOnClickListener {
                selectedColor = element
                view.findViewById<TextView>(R.id.select_color_title).apply {
                    setBackgroundColor(Color.parseColor(element))
                }
                updateSelection()
            }

            view.findViewById<Button>(R.id.remove_color_button).setOnClickListener {
                selectedColor = TASK_DEFAULT_COLOR
                darkSelectedColor = TASK_DEFAULT_DARK_COLOR

                view.findViewById<TextView>(R.id.select_color_title)
                    .setBackgroundColor(getColor(R.color.transparent))

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
                dlg.dismiss()
            }
            .setPositiveButton("OK") { dlg, _ ->
                applyColor()
                setCColor()
                updateTask(
                    task.copy(
                        title = binding.titleEditText.text.toString(),
                        content = binding.contentEditText.text.toString(),
                        color = selectedColor,
                        darkColor = darkSelectedColor
                    )
                )
                dlg.dismiss()
            }
            .setView(view)
            .create()
        dialog.show()

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

    override fun onDestroy() {
        setTextLayoutBackground(TASK_DEFAULT_COLOR)
        super.onDestroy()
    }

    override fun onStop() {
        updateTask(task.copy(lastEdit = formatDate(Date())))
        taskViewModel.updateTask(task)
        Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()
        super.onStop()
    }

    fun showInformationDialog() {
        val string = binding.contentEditText.text.toString()

        var message: String = "Words : ${string.countWord()}\n" +
                "Wrapped lines : ${string.split("\n").size}\n" +
                "Characters : ${string.length}\n" +
                "Characters without whitespaces : ${string.length - string.count { it.isWhitespace() }}\n" +
                "Created at : ${task.createDate}\n" +
                "Last saved at : ${task.lastEdit}"


        val builder = AlertDialog.Builder(this)

        builder
            .setPositiveButton("Ok") { dlg, _ -> dlg.dismiss() }
            .setTitle("Information")
            .setMessage(message)
            .create().show()
    }

    fun updateTask(t: Task) {
        task = t
    }

    fun createCategorizeDialog() {

        val categoryBuilder = AlertDialog.Builder(this)
        categoryBuilder
            .setTitle("Choose category")
        val view = layoutInflater.inflate(R.layout.dialog_category_recycler, null)


        val recycleview = view.findViewById<RecyclerView>(R.id.recyclerView)
        var cAdapter: CategoryDialogAdapter = CategoryDialogAdapter(
            task = task,
            activity = this@AddActivity
        )
        recycleview.adapter = cAdapter

        taskViewModel.categories.observe(this@AddActivity) {
            taskViewModel.getNoteWithCategory(task.taskId).observe(this@AddActivity) { t ->

                if(it != null && t != null) {
                    cAdapter.setCategory(it)
                    if(t.isNotEmpty())cAdapter.setCheckCategory(t[0].categories.toMutableList())
                }
            }
        }
        categoryBuilder.setView(view)
        categoryBuilder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }
        categoryBuilder.create().show()

    }


    private fun applyStyleToSelection(style: Int) {
        val start = binding.contentEditText.selectionStart
        val end = binding.contentEditText.selectionEnd

        if (start < 0 || end <= start) return

        val spannable = binding.contentEditText.text as Spannable
        spannable.setSpan(
            StyleSpan(style),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun applyUnderlineToSelection() {
        val start = binding.contentEditText.selectionStart
        val end = binding.contentEditText.selectionEnd

        if (start < 0 || end <= start) return

        val spannable = binding.contentEditText.text as Spannable
        spannable.setSpan(
            UnderlineSpan(),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private val createDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    writeTxtFile(uri, task.content)
                }
            }
        }

    private fun writeTxtFile(uri: Uri, content: String) {
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
                outputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}