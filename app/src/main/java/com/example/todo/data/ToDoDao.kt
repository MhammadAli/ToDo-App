package com.example.todo.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todo.data.models.ToDoData

@Dao
interface ToDoDao {

    @Query("select * from todo_table order by id asc")
    fun getAllData(): LiveData<List<ToDoData>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(toDoData: ToDoData)

    @Delete
    suspend fun deleteItem(toDoData: ToDoData)

    @Query("delete from todo_table")
    suspend fun deleteAll()

    @Query("select * from todo_table where title like :searchQuery")
    fun searchDatabase(searchQuery: String): LiveData<List<ToDoData>>

    @Query("SELECT * FROM todo_table ORDER BY CASE WHEN priority LIKE 'H%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'L%' THEN 3 END")
    fun sortByHighPriority(): LiveData<List<ToDoData>>

    @Query("SELECT * FROM todo_table ORDER BY CASE WHEN priority LIKE 'L%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'H%' THEN 3 END")
    fun sortByLowPriority(): LiveData<List<ToDoData>>
}