package com.lutech.notepad.ui.add

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.media.Image
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
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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


class AddActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding
    private var isUpdate: Boolean = false
    lateinit var toolbar: Toolbar
    private lateinit var addViewModel: AddViewModel

    private var selectedColor: String = TASK_DEFAULT_COLOR
    private var darkSelectedColor: String = TASK_DEFAULT_DARK_COLOR

    private var cSelectedColor: String = TASK_DEFAULT_COLOR
    private var cDarkSelectedColor: String = TASK_DEFAULT_DARK_COLOR

    private val undoStack: Stack<Spannable> = Stack()

    private var spannable: Spannable? = null

    private lateinit var taskViewModel: TaskViewModel

    private var isBold = false
    private var isItalic = false
    private var isUnderlined = false
    private var isStrikethrough = false
    private var isColorTextChange = false
    private var isBackgroundColorChange = false
    private var isTextSizeChange = false

    private var colorText: String = get64Colors()[0]
    private var backgroundColor: String? = null

    private var isDefaultColorText = true

    private var textColorOpacity: Int = 100
    private var backgroundColorOpacity: Int = 100

    private var textSize = 18

    private var isChangingCharacter = false

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
        val spanned: Spanned = Html.fromHtml(task.content, Html.FROM_HTML_MODE_LEGACY)
        spannable = SpannableString(spanned)
        binding.contentEditText.setText(spannable)

        binding.cancelBtn.setOnClickListener {
            binding.formatBar.visibility = View.GONE
        }

        binding.activityAddBackBtn.setOnClickListener {
            binding.appBarLayout.visibility = View.VISIBLE
        }

        binding.activityAddSearchEditText.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
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
            private var previousText: Spannable? = null
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                startIndex = start
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
                        isColorTextChange || isBackgroundColorChange
                    ) {
                        val start = startIndex!!
                        val end = s.length
                        if (start < end) {
                            removeTextWatcher()
                            formatting(start, end)
                            addTextWatcher()
                            startIndex = end
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

        binding.contentEditText.requestFocus()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    fun setupFormatBarListeners() {
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

    private fun toggleBackgroundFormattingButton(imageView: ImageView, isActive: Boolean) {
        if (!isActive) {
            setActiveBackgroundFormattingButton(imageView)
        } else {
            imageView.background = null
        }
    }

    private fun setActiveBackgroundFormattingButton(imageView: ImageView) {
        imageView.setBackgroundColor(Color.parseColor("#A52A2A99"))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_add_option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //region show dialog

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
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                categoryBuilder.create().show()
            }

            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()


            }

            R.id.action_undo -> {
                if(undoStack.isNotEmpty()) {
                    val previous = undoStack.pop()
                    isChangingCharacter = true
                    binding.contentEditText.setText(previous)
                    binding.contentEditText.setSelection(previous.length)
                    isChangingCharacter = false
                }

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
            }

            R.id.activity_add_action_categorize -> {
                createCategorizeDialog()
            }

            R.id.activity_add_action_show_info -> {
                showInformationDialog()
            }

            R.id.activity_add_action_search -> {
                binding.appBarLayout.visibility = View.INVISIBLE
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

    private fun showTextColorPicker() {
        val view = layoutInflater.inflate(R.layout.pick_color, null)

        val colorGrid = view.findViewById<GridLayout>(R.id.colorGrid).apply {
            rowCount = 8
            columnCount = 8
        }
        var selectColor: String = get64Colors()[0]

        val selectColorTitle = view.findViewById<TextView>(R.id.select_color_title).apply {
            setTextColor(Color.parseColor(colorText))
        }

        view.findViewById<LinearLayout>(R.id.opacity_layout).visibility = View.VISIBLE

        val seekBar = view.findViewById<SeekBar>(R.id.opacity_seek_bar)
        val opacityPer = view.findViewById<TextView>(R.id.opacity_percent)
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textColorOpacity = progress
                opacityPer.text = "Opacity ($textColorOpacity%):"
                selectColorTitle.alpha = (progress / 100f)
                //if(progress == 100) isco
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        val removeBtn = view.findViewById<Button>(R.id.remove_color_button).apply {
            setOnClickListener {
                selectColorTitle.setTextColor(getColor(R.color.black))
                isDefaultColorText = true
                selectColor = get64Colors()[0]
            }
        }

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
                selectColor = element
                selectColorTitle.setTextColor(Color.parseColor(element))
                isDefaultColorText = false
                updateSelection()
            }

            view.findViewById<Button>(R.id.remove_color_button).setOnClickListener {
                //selectedColor = TASK_DEFAULT_COLOR
                //darkSelectedColor = TASK_DEFAULT_DARK_COLOR

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
                //resetCurrentColor()
                dlg.dismiss()
            }
            .setPositiveButton("OK") { dlg, _ ->
                colorText = getColorWithOpacity(selectColor, seekBar.progress / 100f)
                binding.colorTextBtn.setBackgroundColor(Color.parseColor(colorText))
                isColorTextChange = true
                applyTextColor(selectColor)
                //applyColor()
                //setCColor()
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

    private fun showBackgroundColorPickerDialog() {
        val view = layoutInflater.inflate(R.layout.pick_color, null)
        val removeBtn = view.findViewById<Button>(R.id.remove_color_button)
        val colorGrid = view.findViewById<GridLayout>(R.id.colorGrid).apply {
            rowCount = 8
            columnCount = 8
        }
        var selectColor: String = "#ffffff"

        val selectColorTitle = view.findViewById<TextView>(R.id.select_color_title)

        view.findViewById<LinearLayout>(R.id.opacity_layout).visibility = View.VISIBLE

        val seekBar = view.findViewById<SeekBar>(R.id.opacity_seek_bar)
        val opacityPer = view.findViewById<TextView>(R.id.opacity_percent)
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                backgroundColorOpacity = progress
                opacityPer.text = "Opacity (${backgroundColorOpacity}%):"
                selectColorTitle.alpha = (progress / 100f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        removeBtn.setOnClickListener {

        }



        for (element in get64Colors()) {
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = 48
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
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
                selectColor = element
                selectColorTitle.setBackgroundColor(Color.parseColor(element))
                updateSelection()
            }

            view.findViewById<Button>(R.id.remove_color_button).setOnClickListener {
                //selectedColor = TASK_DEFAULT_COLOR
                //darkSelectedColor = TASK_DEFAULT_DARK_COLOR

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
                //resetCurrentColor()
                dlg.dismiss()
            }
            .setPositiveButton("OK") { dlg, _ ->
                isBackgroundColorChange = true
                backgroundColor = getColorWithOpacity(selectColor, backgroundColorOpacity / 100f)
                binding.colorFillBtn.setBackgroundColor(Color.parseColor(backgroundColor))
                applyBackgroundColor(selectColor)
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

    private fun showAdjustTextSizeDialog() {
        val view = layoutInflater.inflate(R.layout.adjust_text_size, null)
        val textTitle = view.findViewById<TextView>(R.id.text_size_title)
        val textSizeBar = view.findViewById<SeekBar>(R.id.text_size_seek_bar)
        val setDefaultBtn = view.findViewById<Button>(R.id.set_default_btn)

        var localTextSize = textSize

        setDefaultBtn.setOnClickListener {
            textSizeBar.progress = 18
            textSize = 18
            localTextSize = 18
        }

        textSizeBar.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    textTitle.setText("Text size ${progress}")
                    textTitle.textSize = progress.toFloat()
                    localTextSize = progress
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

    fun checkTextStyle() {
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

                }

                is BackgroundColorSpan -> {

                }

                is AbsoluteSizeSpan -> {

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
        if(binding.boldBtn !in styles) {
            binding.boldBtn.background = null
            isBold = false
        }
        if(binding.underlinedBtn !in styles) {
            isUnderlined = false
            binding.underlinedBtn.background = null
        }
        if(binding.strikethroughBtn !in styles) {
            isStrikethrough = false
            binding.strikethroughBtn.background = null
        }

//        if(binding.formatSizeBtn !in styles) binding.boldBtn.background = null
//        if(binding.boldBtn !in styles) binding.boldBtn.background = null
//        if(binding.boldBtn !in styles) binding.boldBtn.background = null
    }

    fun resetFormatbarDefault() {
        binding.boldBtn.foreground = null
        binding.boldBtn.alpha = 1f

        binding.italicBtn.foreground = null
        binding.italicBtn.alpha = 1f
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

    fun updateTask(t: Task) {
        task = t.copy(
            content = convertSpannableToHtml()
        )
    }

    fun convertSpannableToHtml(): String {
        val span = binding.contentEditText.text as Spannable
        return Html.toHtml(span, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
    }

    //region apply style
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
    //endregion

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