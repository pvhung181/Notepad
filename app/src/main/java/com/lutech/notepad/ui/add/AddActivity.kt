package com.lutech.notepad.ui.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.lutech.notepad.R
import com.lutech.notepad.adapter.CategoryDialogAdapter
import com.lutech.notepad.constants.DEFAULT_COLOR_BACKGROUND
import com.lutech.notepad.constants.DEFAULT_COLOR_TEXT
import com.lutech.notepad.constants.TASK
import com.lutech.notepad.constants.TASK_CONTENT
import com.lutech.notepad.constants.TASK_CREATION_DATE
import com.lutech.notepad.constants.TASK_DEFAULT_COLOR
import com.lutech.notepad.constants.TASK_DEFAULT_DARK_COLOR
import com.lutech.notepad.constants.TASK_ID
import com.lutech.notepad.constants.TASK_LAST_EDIT
import com.lutech.notepad.constants.TASK_TITLE
import com.lutech.notepad.data.get64Colors
import com.lutech.notepad.data.getColors
import com.lutech.notepad.databinding.ActivityAddBinding
import com.lutech.notepad.listener.CategoryDialogClickListener
import com.lutech.notepad.model.Category
import com.lutech.notepad.model.CategoryTaskCrossRef
import com.lutech.notepad.model.Task
import com.lutech.notepad.ui.TaskViewModel
import com.lutech.notepad.utils.applyBackgroundColorNoSelection
import com.lutech.notepad.utils.applyBoldNoSelection
import com.lutech.notepad.utils.applyForegroundColorNoSelection
import com.lutech.notepad.utils.applyItalicNoSelection
import com.lutech.notepad.utils.applyStrikethroughNoSelection
import com.lutech.notepad.utils.applyTextSizeNoSelection
import com.lutech.notepad.utils.applyUnderlineNoSelection
import com.lutech.notepad.utils.countWord
import com.lutech.notepad.utils.darkenColor
import com.lutech.notepad.utils.formatDate
import com.lutech.notepad.utils.getColorWithOpacity
import java.io.IOException
import java.util.Date
import java.util.Stack
import kotlin.math.max
import kotlin.math.min


class AddActivity : AppCompatActivity(), CategoryDialogClickListener {
    lateinit var binding: ActivityAddBinding

    lateinit var toolbar: Toolbar

    private lateinit var addViewModel: AddViewModel
    private lateinit var taskViewModel: TaskViewModel

    private val undoStack: Stack<Spannable> = Stack()

    private var selectedColor: String = TASK_DEFAULT_COLOR
    private var darkSelectedColor: String = TASK_DEFAULT_DARK_COLOR
    private var cSelectedColor: String = TASK_DEFAULT_COLOR
    private var cDarkSelectedColor: String = TASK_DEFAULT_DARK_COLOR
    private var colorText: String = DEFAULT_COLOR_TEXT
    private var backgroundColor: String = DEFAULT_COLOR_BACKGROUND
    private var textColorOpacity: Int = 100
    private var backgroundColorOpacity: Int = 100
    private var textSize = 18

    private var spannable: Spannable? = null

    private var isBold = false
    private var isItalic = false
    private var isUnderlined = false
    private var isStrikethrough = false
    private var isColorTextChange = false
    private var isBackgroundColorChange = false
    private var isTextSizeChange = false

    private var isChangingCharacter = false
    private var isUpdate: Boolean = false

    var task: Task = Task()
    var original: Task = Task()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initValue()
        setupFormatBarListeners()
        setupReadonlyToolbar()
        setListeners()
    }

    //region init
    private fun init() {
        addViewModel = ViewModelProvider(this)[AddViewModel::class.java]
        isUpdate = intent.getBundleExtra(TASK) != null
        toolbar = binding.toolbar
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
    }

    private fun initValue() {
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
            original = task.copy()
            setCColor()
            applyColorForNote()
        }

        Log.i("TASK", task.content)
        binding.titleEditText.setText(task.title)
        val spanned: Spanned = Html.fromHtml(task.content, Html.FROM_HTML_MODE_LEGACY)
        Log.i("TASK", spanned.toString())
        spannable = SpannableString(spanned)
        Log.i("TASK", spannable.toString())
        binding.contentEditText.setText(spannable)


        binding.contentEditText.requestFocus()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    //endregion

    //region show dialog

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
                applyColorForNote()
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

    private fun showTextColorPicker() {
        val view = layoutInflater.inflate(R.layout.pick_color, null)
        var isReset = false
        val colorGrid = view.findViewById<GridLayout>(R.id.colorGrid).apply {
            rowCount = 8
            columnCount = 8
        }
        var localTextOpacity = 100
        var localSelectedColor: String = DEFAULT_COLOR_TEXT

        val selectColorTitle = view.findViewById<TextView>(R.id.select_color_title).apply {
            setTextColor(Color.parseColor(colorText))
        }

        view.findViewById<LinearLayout>(R.id.opacity_layout).visibility = View.VISIBLE

        val removeBtn = view.findViewById<Button>(R.id.remove_color_button)
        val seekBar = view.findViewById<SeekBar>(R.id.opacity_seek_bar)
        val opacityPer = view.findViewById<TextView>(R.id.opacity_percent)

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!isReset) {
                    localTextOpacity = progress
                    selectColorTitle.alpha = (progress / 100f)
                }
                opacityPer.text = "Opacity ($progress%):"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })



        for (element in get64Colors()) {
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = 48
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                //setMargins(2, 2, 2, 2)
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
                textSize = 24f
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

                    if (get64Colors()[i] == selectedColor) {
                        plusText.visibility = View.VISIBLE
                        darkSelectedColor = darkenColor(selectedColor)
                    } else plusText.visibility = View.INVISIBLE
                }
            }

            b.setOnClickListener {
                localSelectedColor = element
                selectColorTitle.setTextColor(Color.parseColor(element))
                isReset = false
                updateSelection()
            }

            colorGrid.addView(frame)
        }

        val builder = AlertDialog.Builder(this)


        val dialog = builder
            .setNegativeButton("Cancel") { dlg, _ ->
                //resetCurrentColor()
                dlg.dismiss()
            }
            .setPositiveButton("OK") { dlg, _ ->
                if (isReset) {
                    colorText = DEFAULT_COLOR_TEXT
                    binding.colorTextBtn.background = null
                    isColorTextChange = false
                } else {
                    colorText = getColorWithOpacity(localSelectedColor, seekBar.progress / 100f)
                    //Toast.makeText(this@AddActivity, colorText, Toast.LENGTH_SHORT).show()
                    textColorOpacity = localTextOpacity
                    binding.colorTextBtn.setBackgroundColor(Color.parseColor(colorText))
                    isColorTextChange = true

                }
                applyTextColor(localSelectedColor)
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
        dialog.setOnShowListener {
            removeBtn.setOnClickListener {

                for (i in 0 until colorGrid.childCount) {
                    val frame = colorGrid.getChildAt(i) as FrameLayout
                    val plusText = frame.getChildAt(1) as TextView
                    plusText.visibility = View.INVISIBLE
                }

                localSelectedColor = DEFAULT_COLOR_TEXT
                selectColorTitle.setTextColor(getColor(R.color.black))
                localTextOpacity = 100
                seekBar.progress = localTextOpacity
                isReset = true
                //Toast.makeText(this@AddActivity, "active", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()

    }

    private fun showBackgroundColorPickerDialog() {
        val view = layoutInflater.inflate(R.layout.pick_color, null)
        var isReset = false
        val colorGrid = view.findViewById<GridLayout>(R.id.colorGrid).apply {
            rowCount = 8
            columnCount = 8
        }
        var localBackgroundOpacity = 100
        var localSelectedColor: String = backgroundColor

        val selectColorTitle = view.findViewById<TextView>(R.id.select_color_title).apply {
            setBackgroundColor(Color.parseColor(backgroundColor))
        }

        view.findViewById<LinearLayout>(R.id.opacity_layout).visibility = View.VISIBLE

        val removeBtn = view.findViewById<Button>(R.id.remove_color_button)
        val seekBar = view.findViewById<SeekBar>(R.id.opacity_seek_bar)
        val opacityPer = view.findViewById<TextView>(R.id.opacity_percent)

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!isReset) {
                    localBackgroundOpacity = progress
                    selectColorTitle.alpha = (progress / 100f)
                }
                opacityPer.text = "Opacity ($progress%):"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })



        for (element in get64Colors()) {
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = 48
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                //setMargins(2, 2, 2, 2)
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
                textSize = 24f
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

                    if (get64Colors()[i] == selectedColor) {
                        plusText.visibility = View.VISIBLE
                        darkSelectedColor = darkenColor(selectedColor)
                    } else plusText.visibility = View.INVISIBLE
                }
            }

            b.setOnClickListener {
                localSelectedColor = element
                selectColorTitle.setBackgroundColor(Color.parseColor(element))
                isReset = false
                updateSelection()
            }

            colorGrid.addView(frame)
        }

        val builder = AlertDialog.Builder(this)


        val dialog = builder
            .setNegativeButton("Cancel") { dlg, _ ->
                //resetCurrentColor()
                dlg.dismiss()
            }
            .setPositiveButton("OK") { dlg, _ ->
                if (isReset) {
                    backgroundColor = DEFAULT_COLOR_BACKGROUND
                    binding.colorFillBtn.background = null
                    isBackgroundColorChange = false
                } else {
                    backgroundColor =
                        getColorWithOpacity(localSelectedColor, seekBar.progress / 100f)
                    //Toast.makeText(this@AddActivity, colorText, Toast.LENGTH_SHORT).show()
                    backgroundColorOpacity = localBackgroundOpacity
                    binding.colorFillBtn.setBackgroundColor(Color.parseColor(backgroundColor))
                    isBackgroundColorChange = true

                }
                applyBackgroundColor(localSelectedColor)
                updateTask(
                    task.copy(
                        title = binding.titleEditText.text.toString(),
                        content = binding.contentEditText.text.toString(),
                    )
                )
                dlg.dismiss()
            }
            .setView(view)
            .create()
        dialog.setOnShowListener {
            removeBtn.setOnClickListener {

                for (i in 0 until colorGrid.childCount) {
                    val frame = colorGrid.getChildAt(i) as FrameLayout
                    val plusText = frame.getChildAt(1) as TextView
                    plusText.visibility = View.INVISIBLE
                }

                localSelectedColor = DEFAULT_COLOR_BACKGROUND
                selectColorTitle.setBackgroundColor(getColor(R.color.transparent))
                localBackgroundOpacity = 100
                seekBar.progress = localBackgroundOpacity
                isReset = true
                //Toast.makeText(this@AddActivity, "active", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()


    }

    private fun showAdjustTextSizeDialog() {
        val view = layoutInflater.inflate(R.layout.adjust_text_size, null)
        val textTitle = view.findViewById<TextView>(R.id.text_size_title)
        val textSizeBar = view.findViewById<SeekBar>(R.id.text_size_seek_bar)
        val setDefaultBtn = view.findViewById<Button>(R.id.set_default_btn)

        var localTextSize = textSize

        textTitle.textSize = localTextSize.toFloat()
        textTitle.setText("Text size $localTextSize")

        setDefaultBtn.setOnClickListener {
            textSizeBar.progress = 18
            textSize = 18
            localTextSize = 18
        }

        textSizeBar.progress = localTextSize
        textSizeBar.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    localTextSize = progress
                    textTitle.setText("Text size ${localTextSize}")
                    textTitle.textSize = localTextSize.toFloat()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            }
        )

        val builder = AlertDialog.Builder(this)


        val dialog = builder
            .setNegativeButton("Cancel") { dlg, _ ->
                dlg.dismiss()
            }
            .setPositiveButton("OK") { dlg, _ ->
                if (localTextSize == 18) {
                    isTextSizeChange = false
                    textSize = 18
                } else {
                    isTextSizeChange = true
                    textSize = localTextSize
                    toggleBackgroundFormattingButton(binding.formatSizeBtn, false)
                }

                applyTextSize(textSizeBar.progress)
                updateTask(
                    task.copy(
                        title = binding.titleEditText.text.toString(),
                        content = binding.contentEditText.text.toString(),
                    )
                )
                dlg.dismiss()
            }
            .setView(view)
            .create()
        dialog.show()


    }

    private fun showInformationDialog() {
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

    private fun createCategorizeDialog() {

        val categoryBuilder = AlertDialog.Builder(this)
        categoryBuilder
            .setTitle("Choose category")
        val view = layoutInflater.inflate(R.layout.dialog_category_recycler, null)


        val recycleview = view.findViewById<RecyclerView>(R.id.recyclerView)
        var cAdapter = CategoryDialogAdapter(
            task = task,
            listener = this
        )
        recycleview.adapter = cAdapter

        taskViewModel.categories.observe(this@AddActivity) {
            taskViewModel.getNoteWithCategory(task.taskId).observe(this@AddActivity) { t ->

                if (it != null && t != null) {
                    cAdapter.setCategory(it)
                    if (t.isNotEmpty()) cAdapter.setCheckCategory(t[0].categories.toMutableList())
                }
            }
        }
        categoryBuilder.setView(view)
        categoryBuilder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }
        categoryBuilder.create().show()
    }

    //endregion

    //region style utilities

    private fun highlightText(editText: EditText, textToHighlight: String) {
        val spannableString = editText.text

        val spans =
            spannableString.getSpans(0, spannableString.length, BackgroundColorSpan::class.java)
        for (span in spans) {
            if (spannableString.getSpanStart(span) != -1 && spannableString.getSpanEnd(span) != -1) {
                spannableString.removeSpan(span)
            }
        }
        if (textToHighlight.isBlank()) {
            val s = SpannableString(Html.fromHtml(task.content, Html.FROM_HTML_MODE_LEGACY))
            isChangingCharacter = true
            binding.contentEditText.setText(s)
            isChangingCharacter = false
            return
        }

        var startIndex = spannableString.indexOf(textToHighlight)
        while (startIndex >= 0) {
            val endIndex = startIndex + textToHighlight.length

            spannableString.setSpan(
                BackgroundColorSpan(Color.YELLOW),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            startIndex = spannableString.indexOf(textToHighlight, endIndex)
        }
    }

    private fun resetCurrentColor() {
        selectedColor = cSelectedColor
        darkSelectedColor = cDarkSelectedColor
    }

    private fun applyColorForNote() {
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

    private fun convertSpannableToHtml(): String {
        val span = binding.contentEditText.text as Spannable
        return Html.toHtml(span, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
    }

    //endregion

    //region menu
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
                original = task.copy()
                undoStack.clear()

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
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                categoryBuilder.create().show()
            }

            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
            }

            R.id.action_undo -> {
                if (undoStack.isNotEmpty()) {
                    val previous = undoStack.pop()
                    isChangingCharacter = true
                    binding.contentEditText.setText(previous)
                    binding.contentEditText.setSelection(previous.length)
                    isChangingCharacter = false
                }
            }

            R.id.activity_add_action_undo_all -> {
                task = original.copy()
                isChangingCharacter = true
                binding.contentEditText.setText(
                    SpannableString(
                        Html.fromHtml(
                            task.content,
                            Html.FROM_HTML_MODE_LEGACY
                        )
                    )
                )
                binding.titleEditText.setText(task.title)
                undoStack.clear()
                isChangingCharacter = false
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
                binding.readOnlyToolbar.setBackgroundColor(Color.parseColor(task.darkColor))
                binding.titleEditText.isEnabled = false
                binding.contentEditText.isEnabled = false
                binding.contentEditText.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.titleEditText.setTextColor(ContextCompat.getColor(this, R.color.black))

                binding.appBarLayout.visibility = View.INVISIBLE
                binding.readOnlyToolbar.visibility = View.VISIBLE

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.contentEditText.windowToken, 0)
            }

            R.id.activity_add_action_categorize -> {
                createCategorizeDialog()
            }

            R.id.activity_add_action_show_info -> {
                showInformationDialog()
            }

            R.id.activity_add_action_search -> {
                binding.activityAddSearchField.setBackgroundColor(Color.parseColor(task.darkColor))
                binding.appBarLayout.visibility = View.INVISIBLE
                binding.readOnlyToolbar.visibility = View.INVISIBLE
                binding.activityAddSearchEditText.requestFocus()
            }

            R.id.activity_add_action_export -> {
                exportNote()
            }

            R.id.activity_add_action_format_bar -> {
                if (binding.formatBar.visibility == View.GONE) binding.formatBar.visibility =
                    View.VISIBLE
                else binding.formatBar.visibility = View.GONE
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    //region setup
    private fun setListeners() {
        binding.cancelBtn.setOnClickListener {
            binding.formatBar.visibility = View.GONE
        }

        binding.activityAddBackBtn.setOnClickListener {
            binding.appBarLayout.visibility = View.VISIBLE
            binding.activityAddSearchEditText.setQuery("", false)
        }

        binding.activityAddSearchEditText.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.activityAddSearchEditText.setQuery("", false)
                binding.appBarLayout.visibility = View.VISIBLE
            }
        }

        binding.activityAddSearchEditText.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    highlightText(binding.contentEditText, newText ?: "")
                    return true
                }

            }
        )

        binding.titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                updateTask(task.copy(title = s.toString()))

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        val textWatcher = object : TextWatcher {
            var startIndex: Int? = null
            var endIndex: Int? = null
            private var previousText: Spannable? = null
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                startIndex = start
                endIndex = start + after
                previousText = if (s is Spannable) {
                    SpannableStringBuilder(s)
                } else {
                    SpannableStringBuilder.valueOf(s)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTask(task.copy(content = s.toString()))
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.toString() != previousText.toString() && !isChangingCharacter) {
                        undoStack.push(previousText)
                    }

                    if (isBold || isItalic || isUnderlined || isStrikethrough ||
                        isColorTextChange || isBackgroundColorChange || isTextSizeChange
                    ) {
                        val start = startIndex!!
                        val end = endIndex!!
                        if (start < end) {
                            removeTextWatcher()
                            formatting(start, end)

                            addTextWatcher()
                            Toast.makeText(this@AddActivity, task.content, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            private fun removeTextWatcher() {
                binding.contentEditText.removeTextChangedListener(this)
            }

            private fun addTextWatcher() {
                binding.contentEditText.addTextChangedListener(this)
            }

        }

        binding.contentEditText.addTextChangedListener(textWatcher)

        binding.contentEditText.setOnClickListener {
            checkTextStyle()
        }
    }

    private fun setupFormatBarListeners() {
        binding.boldBtn.setOnClickListener {
            toggleBackgroundFormattingButton(binding.boldBtn, isBold)
            applyStyle(Typeface.BOLD)
            isBold = !isBold
        }

        binding.italicBtn.setOnClickListener {
            toggleBackgroundFormattingButton(binding.italicBtn, isItalic)
            applyStyle(Typeface.ITALIC)
            isItalic = !isItalic
        }

        binding.underlinedBtn.setOnClickListener {
            toggleBackgroundFormattingButton(binding.underlinedBtn, isUnderlined)
            applyUnderline()
            isUnderlined = !isUnderlined
        }

        binding.strikethroughBtn.setOnClickListener {
            toggleBackgroundFormattingButton(binding.strikethroughBtn, isStrikethrough)
            applyStrikethrough()
            isStrikethrough = !isStrikethrough
        }

        binding.colorFillBtn.setOnClickListener {
            showBackgroundColorPickerDialog()
        }

        binding.colorTextBtn.setOnClickListener {
            showTextColorPicker()
        }

        binding.formatSizeBtn.setOnClickListener {
            showAdjustTextSizeDialog()
        }
    }

    fun setupReadonlyToolbar() {
        binding.activityAddBackBtnReadonly.setOnClickListener {
            binding.appBarLayout.visibility = View.VISIBLE
            binding.titleEditText.isEnabled = true
            binding.contentEditText.isEnabled = true
        }

        binding.readonlyEdit.setOnClickListener {
            binding.appBarLayout.visibility = View.VISIBLE
            binding.titleEditText.isEnabled = true
            binding.contentEditText.isEnabled = true
            binding.contentEditText.requestFocus()
        }

        binding.readonlyDownload.setOnClickListener {
            exportNote()
        }

    }

    //endregion

    //region edit text style
    private fun applyStyle(style: Int) {
        val text = binding.contentEditText.text
        if (text is Spannable) {
            var start = binding.contentEditText.selectionStart
            var end = binding.contentEditText.selectionEnd
            var minStart = binding.contentEditText.text.length
            var maxEnd = 0

            val styleSpans = text.getSpans(start, end, StyleSpan::class.java)
            var styleExists = false
            for (span in styleSpans) {
                minStart = min(minStart, text.getSpanStart(span))
                maxEnd = max(maxEnd, text.getSpanEnd(span))

                if (span.style == style) {
                    text.removeSpan(span)
                    styleExists = true
                }
            }

            if (start > minStart) start = minStart
            if (end < maxEnd) end = maxEnd

            if (!styleExists) {
                text.setSpan(StyleSpan(style), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun applyTextSize(size: Int) {
        val start = binding.contentEditText.selectionStart
        val end = binding.contentEditText.selectionEnd

        if (start < 0 || end <= start) return

        val span = binding.contentEditText.text as Spannable


        span.setSpan(
            AbsoluteSizeSpan(size, true),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

    }

    private fun applyBackgroundColor(hex: String) {
        val start = binding.contentEditText.selectionStart
        val end = binding.contentEditText.selectionEnd

        if (start < 0 || end <= start) return

        val span = binding.contentEditText.text as Spannable


        span.setSpan(
            BackgroundColorSpan(Color.parseColor(hex)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun applyUnderline() {
        val start = binding.contentEditText.selectionStart
        val end = binding.contentEditText.selectionEnd

        if (start < end) {
            val spannable = binding.contentEditText.text as Spannable
            val spans = spannable.getSpans(start, end, UnderlineSpan::class.java)

            if (spans.isNotEmpty()) {
                for (span in spans) {
                    spannable.removeSpan(span)
                }
            } else {
                spannable.setSpan(
                    UnderlineSpan(),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    private fun applyTextColor(hex: String) {
        val start = binding.contentEditText.selectionStart
        val end = binding.contentEditText.selectionEnd

        if (start < 0 || end <= start) return

        val span = binding.contentEditText.text as Spannable

        span.setSpan(
            ForegroundColorSpan(Color.parseColor(hex)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun applyStrikethrough() {
        val start = binding.contentEditText.selectionStart
        val end = binding.contentEditText.selectionEnd

        if (start < end) {
            val spannable = binding.contentEditText.text as Spannable
            val spans = spannable.getSpans(start, end, StrikethroughSpan::class.java)

            if (spans.isNotEmpty()) {
                for (span in spans) {
                    spannable.removeSpan(span)
                }
            } else {
                spannable.setSpan(
                    StrikethroughSpan(),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    private fun checkTextStyle() {
        val start = binding.contentEditText.selectionStart
        val text = binding.contentEditText.text as Spannable

        val spans = text.getSpans(start, start, CharacterStyle::class.java)

        val styles = mutableListOf<ImageView>()

        for (span in spans) {
            when (span) {
                is StyleSpan -> {
                    if (span.style == Typeface.BOLD) {
                        styles.add(binding.boldBtn)
                        isBold = true
                    }

                    if (span.style == Typeface.ITALIC) {
                        isItalic = true
                        styles.add(binding.italicBtn)
                    }

                }

                is UnderlineSpan -> {
                    styles.add(binding.underlinedBtn)
                    isUnderlined = true
                }

                is StrikethroughSpan -> {
                    styles.add(binding.strikethroughBtn)
                    isStrikethrough = true
                }

                is ForegroundColorSpan -> {
                    styles.add(binding.colorTextBtn)
                    val spanColor = span.foregroundColor
                    val hexColor = String.format("#%08X", spanColor)
                    binding.colorTextBtn.setBackgroundColor(Color.parseColor(hexColor))
                    colorText = hexColor
                    isColorTextChange = true
                }

                is BackgroundColorSpan -> {
                    styles.add(binding.colorFillBtn)
                    val spanColor = span.backgroundColor
                    val hexColor = String.format("#%08X", spanColor)
                    binding.colorFillBtn.setBackgroundColor(Color.parseColor(hexColor))
                    backgroundColor = hexColor
                    isBackgroundColorChange = true
                }

                is AbsoluteSizeSpan -> {
                    styles.add(binding.formatSizeBtn)
                    textSize = span.size
                    isTextSizeChange = true
                }


            }
        }

        styles.forEach {
            setActiveBackgroundFormattingButton(it)
        }

        if (binding.italicBtn !in styles) {
            binding.italicBtn.background = null
            isItalic = false
        }
        if (binding.boldBtn !in styles) {
            binding.boldBtn.background = null
            isBold = false
        }
        if (binding.underlinedBtn !in styles) {
            isUnderlined = false
            binding.underlinedBtn.background = null
        }
        if (binding.strikethroughBtn !in styles) {
            isStrikethrough = false
            binding.strikethroughBtn.background = null
        }

        if (binding.colorTextBtn !in styles) {
            binding.colorTextBtn.background = null
            isColorTextChange = false
        }

        if (binding.colorFillBtn !in styles) {
            binding.colorFillBtn.background = null
            isBackgroundColorChange = false
        }

        if (binding.formatSizeBtn !in styles) {
            binding.formatSizeBtn.background = null
            textSize = 18
            isTextSizeChange = false
        }

    }

    private fun formatting(start: Int, end: Int) {
        if (isBold) {
            applyBoldNoSelection(
                binding.contentEditText,
                start,
                end
            )
        }

        if (isItalic) {
            applyItalicNoSelection(
                binding.contentEditText,
                start,
                end
            )
        }

        if (isUnderlined) {
            applyUnderlineNoSelection(
                binding.contentEditText,
                start,
                end
            )
        }

        if (isStrikethrough) {
            applyStrikethroughNoSelection(
                binding.contentEditText,
                start,
                end
            )
        }

        if (isColorTextChange) {
            applyForegroundColorNoSelection(
                binding.contentEditText,
                start,
                end,
                Color.parseColor(colorText)
            )
        }
        if (isBackgroundColorChange) {
            applyBackgroundColorNoSelection(
                binding.contentEditText,
                start,
                end,
                Color.parseColor(backgroundColor)
            )
        }

        if (isTextSizeChange) {
            applyTextSizeNoSelection(
                binding.contentEditText,
                start,
                end,
                textSize
            )
        }
    }

    private fun toggleBackgroundFormattingButton(imageView: ImageView, isActive: Boolean) {
        if (!isActive) {
            setActiveBackgroundFormattingButton(imageView)
        } else {
            imageView.background = null
        }
    }

    private fun setActiveBackgroundFormattingButton(imageView: ImageView) {
        if (imageView == binding.colorFillBtn) {

        } else if (imageView == binding.colorTextBtn) {

        } else imageView.setBackgroundColor(Color.parseColor("#afaeac"))
    }

    //endregion

    //region I/O
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

    private fun exportNote() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "${title}.txt")
        }
        createDocumentLauncher.launch(intent)
    }
    //endregion

    //region function utilities
    fun updateTask(t: Task) {
        task = t.copy(
            content = convertSpannableToHtml()
        )
    }
    //endregion

    //region activity life cycle
    override fun onDestroy() {
        setTextLayoutBackground(TASK_DEFAULT_COLOR)
        super.onDestroy()
    }

    override fun onStop() {
        if (original != task) {
            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()
            updateTask(task.copy(lastEdit = formatDate(Date())))
            taskViewModel.updateTask(task)
        }
        super.onStop()
    }
    //endregion


    override fun onCheckboxClick(category: Category ,isChecked: Boolean) {
        if(isChecked) taskViewModel.insertCategoryNote(CategoryTaskCrossRef(category.categoryId, task.taskId))
        else taskViewModel.deleteCategoryNote(CategoryTaskCrossRef(category.categoryId, task.taskId))
    }
}
