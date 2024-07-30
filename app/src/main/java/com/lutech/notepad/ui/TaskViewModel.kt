package com.lutech.notepad.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lutech.notepad.database.AppDatabase
import com.lutech.notepad.database.repository.CategoryNoteRepository
import com.lutech.notepad.database.repository.CategoryNoteRepositoryImpl
import com.lutech.notepad.database.repository.CategoryRepository
import com.lutech.notepad.database.repository.CategoryRepositoryImpl
import com.lutech.notepad.database.repository.TaskRepositoryImpl
import com.lutech.notepad.model.Category
import com.lutech.notepad.model.CategoryTaskCrossRef
import com.lutech.notepad.model.CategoryWithNotes
import com.lutech.notepad.model.NoteWithCategories
import com.lutech.notepad.model.Task
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TaskViewModel(
    application: Application
) : AndroidViewModel(application) {
    private var mutableLiveData = MutableLiveData<String>()

    private val repository: TaskRepositoryImpl =
        TaskRepositoryImpl(AppDatabase.getDatabase(application).taskDao())

    private val categoryRepository: CategoryRepositoryImpl =
        CategoryRepositoryImpl((AppDatabase.getDatabase(application).categoryDao()))

    private val categoryNoteRepository: CategoryNoteRepository =
        CategoryNoteRepositoryImpl(AppDatabase.getDatabase(application).categoryNoteDao())

    private val _tasks = repository.getAllTask()

    val tasks: LiveData<MutableList<Task>> = _tasks

    var categories: LiveData<MutableList<Category>> = categoryRepository.getAllCategory()
    var noteWithCategories: LiveData<MutableList<NoteWithCategories>>? = null

    lateinit var lastTask: Task

    fun setText(s: String) {
        mutableLiveData.value = s
    }

    fun getText(): MutableLiveData<String> {
        return mutableLiveData
    }


    fun moveToTrash(task: Task) {
        viewModelScope.launch {
            repository.updateTask(
                task.copy(
                    isDeleted = true
                )
            )
        }
    }

    fun restoreTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(
                task.copy(isDeleted = false)
            )
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun insertTask(task: Task): Job {
       return viewModelScope.launch {
             repository.saveTask(task)
        }
    }

    fun getLastTask(): Job {
        return viewModelScope.launch {
           lastTask = repository.getLastTask()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
        }
    }


    fun getNoteWithCategory(id: Int): LiveData<List<NoteWithCategories>> {
        return categoryNoteRepository.getNoteWithCategory(id)
    }

    fun getCategoryWithNote(id: Int): LiveData<List<CategoryWithNotes>> {
        return categoryNoteRepository.getCategoryWithNote(id)
    }


    fun insertCategoryNote(categoryTaskCrossRef: CategoryTaskCrossRef) {
        viewModelScope.launch {
            categoryNoteRepository.insertCategoryNote(listOf(categoryTaskCrossRef))
        }
    }

    fun deleteCategoryNote(categoryTaskCrossRef: CategoryTaskCrossRef) {
        viewModelScope.launch {
            categoryNoteRepository.delete(categoryTaskCrossRef)
        }
    }

}