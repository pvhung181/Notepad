package com.lutech.notepad.listener

import com.lutech.notepad.model.Category

interface CategoryItemClickListener {
    fun onEditButtonClick(category: Category)
    fun onDeleteButtonClick(category: Category)
}