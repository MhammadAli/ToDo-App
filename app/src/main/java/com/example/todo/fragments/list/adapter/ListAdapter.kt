package com.example.todo.fragments.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.data.models.ToDoData
import com.example.todo.databinding.RowLayoutBinding

class ListAdapter :
    androidx.recyclerview.widget.ListAdapter<ToDoData, ListAdapter.MyViewHolder>(DiffCallBack) {

    class MyViewHolder(private val binding: RowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(toDoData: ToDoData) {

            binding.toDoData = toDoData
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)

            }
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<ToDoData>() {
        override fun areItemsTheSame(oldItem: ToDoData, newItem: ToDoData): Boolean =
            oldItem == newItem


        override fun areContentsTheSame(oldItem: ToDoData, newItem: ToDoData): Boolean =
            oldItem.id == newItem.id

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder.from(parent)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

}