package com.lutech.notepad.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.lutech.notepad.database.AppDatabase
import com.lutech.notepad.database.repository.CategoryRepository
import com.lutech.notepad.database.repository.CategoryRepositoryImpl
import com.lutech.notepad.model.Category
import kotlinx.coroutines.launch

class CategoryViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository: CategoryRepository = CategoryRepositoryImpl(AppDatabase.getDatabase(application).categoryDao())

    private val _categories = repository.getAllCategory()

    val categories: LiveData<MutableList<Category>> = _categories

    var categoryCheck: MutableList<Category>? = null

    fun insertCategory(category: Category) {
        if(category.categoryName.trim().isBlank()) return
        for(c in categoryCheck!!)
            if(c.categoryName == category.categoryName) return
        viewModelScope.launch {
            repository.insertCategory(category)
        }
    }
}