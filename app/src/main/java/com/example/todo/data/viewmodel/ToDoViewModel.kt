package com.example.todo.data.viewmodel

import android.app.Application
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todo.R
import com.example.todo.data.ToDoDatabase
import com.example.todo.data.models.Priority
import com.example.todo.data.models.ToDoData
import com.example.todo.data.repository.ToDoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoViewModel(application: Application) : AndroidViewModel(application) {

    private val toDoDao = ToDoDatabase.getDatabase(application).toDoDao()
    private val repository: ToDoRepository = ToDoRepository(toDoDao)
    val getAllData: LiveData<List<ToDoData>> = repository.getAllData
    val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(false)
    val sortByHighPriority: LiveData<List<ToDoData>> = repository.sortByHighPriority
    val sortByLowPriority: LiveData<List<ToDoData>> = repository.sortByLowPriority

    fun checkIfDatabaseEmpty(toDoData: List<ToDoData>) {
        emptyDatabase.value = toDoData.isEmpty()
    }

    val listener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (position) {
                0 -> {
                    (parent?.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            application,
                            R.color.red
                        )
                    )
                }
                1 -> {
                    (parent?.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            application,
                            R.color.yellow
                        )
                    )
                }
                2 -> {
                    (parent?.getChildAt(0) as TextView).setTextColor(
                        ContextCompat.getColor(
                            application,
                            R.color.green
                        )
                    )
                }
            }

        }

        override fun onNothingSelected(p0: AdapterView<*>?) {

        }
    }

    fun upsert(toDoData: ToDoData) = viewModelScope.launch(Dispatchers.IO) {
        repository.upsert(toDoData)
    }

    fun deleteItem(toDoData: ToDoData) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteItem(toDoData)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

    fun searchDatabase(searchQuery: String): LiveData<List<ToDoData>> =
        repository.searchDatabase(searchQuery)


    fun verifyDataFromUser(title: String, description: String): Boolean =
        !(TextUtils.isEmpty(title) || TextUtils.isEmpty(description))


    fun parsePriority(priority: String): Priority = when (priority) {
        "High Priority" -> Priority.HIGH
        "Medium Priority" -> Priority.MEDIUM
        "Low Priority" -> Priority.LOW
        else -> Priority.LOW
    }

}