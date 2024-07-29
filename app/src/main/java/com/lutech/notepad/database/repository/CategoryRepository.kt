package com.lutech.notepad.database.repository

import androidx.lifecycle.LiveData
import com.lutech.notepad.database.dao.CategoryDao
import com.lutech.notepad.model.Category

interface CategoryRepository {

    fun getAllCategory(): LiveData<MutableList<Category>>

    suspend fun insertCategory(category: Category)

    suspend fun updateCategory(category: Category)

    suspend fun deleteCategory(category: Category)

}

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository{
    override fun getAllCategory(): LiveData<MutableList<Category>> {
        return categoryDao.getAllCategory()
    }

    override suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category)
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

}
