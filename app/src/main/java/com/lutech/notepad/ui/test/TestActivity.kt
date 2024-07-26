package com.lutech.notepad.ui.test

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import com.lutech.notepad.R
import com.lutech.notepad.data.getColors
import com.lutech.notepad.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    private lateinit var colorGrid: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)

        val colorsProvider = getColors()

        setContentView(binding.root)
        colorGrid = binding.colorGrid

        for (element in colorsProvider) {
            val b = Button(this).apply {
                setBackgroundColor(Color.parseColor(element))
            }
            colorGrid.addView(b)
        }
    }
}