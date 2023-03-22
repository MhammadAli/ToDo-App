package com.example.todo.data.repository

import androidx.lifecycle.LiveData
import com.example.todo.data.ToDoDao
import com.example.todo.data.models.ToDoData

class ToDoRepository(private val toDoDao: ToDoDao) {

    suspend fun upsert(toDoData: ToDoData) = toDoDao.upsert(toDoData)
    suspend fun deleteItem(toDoData: ToDoData) = toDoDao.deleteItem(toDoData)
    suspend fun deleteAll() = toDoDao.deleteAll()
    fun searchDatabase(searchQuery: String): LiveData<List<ToDoData>> =
        toDoDao.searchDatabase(searchQuery)

    val getAllData: LiveData<List<ToDoData>> = toDoDao.getAllData()
    val sortByHighPriority:LiveData<List<ToDoData>> = toDoDao.sortByHighPriority()
    val sortByLowPriority:LiveData<List<ToDoData>> = toDoDao.sortByLowPriority()
}