package com.lutech.notepad.listener

import com.lutech.notepad.model.Category

interface CategoryDialogClickListener {
    fun onCheckboxClick(category: Category, isChecked: Boolean)
}