package com.example.todo.fragments

import android.view.View
import android.widget.Spinner
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.example.todo.R
import com.example.todo.data.models.Priority
import com.example.todo.data.models.ToDoData
import com.example.todo.fragments.list.ListFragmentDirections
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BindingAdapter {

    companion object {

        @BindingAdapter("android:parsePriorityColor")
        @JvmStatic
        fun parsePriorityColor(cardView: CardView, priority: Priority) {
            when (priority) {
                Priority.LOW -> {
                    cardView.setCardBackgroundColor(cardView.context.getColor(R.color.green))
                }
                Priority.MEDIUM -> {
                    cardView.setCardBackgroundColor(cardView.context.getColor(R.color.yellow))
                }
                else -> {
                    cardView.setCardBackgroundColor(cardView.context.getColor(R.color.red))
                }
            }
        }


        @BindingAdapter("android:parsePriorityToInt")
        @JvmStatic
        fun parsePriorityToInt(spinner: Spinner, priority: Priority) {
            when (priority) {
                Priority.HIGH -> {
                    spinner.setSelection(0)
                }
                Priority.MEDIUM -> {
                    spinner.setSelection(1)
                }
                else -> {
                    spinner.setSelection(2)
                }
            }
        }

        @BindingAdapter("android:sendDataToUpdateFragment")
        @JvmStatic
        fun sendDataToUpdateFragment(view: ConstraintLayout, currentItem: ToDoData) {
            view.setOnClickListener {
                val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
                view.findNavController().navigate(action)
            }
        }

        @BindingAdapter("android:emptyDatabase")
        @JvmStatic
        fun emptyDatabase(view: View, items: MutableLiveData<Boolean>) {
            when (items.value) {
                true -> view.visibility = View.VISIBLE
                false -> view.visibility = View.INVISIBLE
                else -> {}
            }
        }

        @BindingAdapter("android:navigateToAddFragment")
        @JvmStatic
        fun navigateToAddFragment(fab: FloatingActionButton, navigate: Boolean) {
            if (navigate) {
                fab.setOnClickListener {
                    fab.findNavController().navigate(R.id.action_listFragment_to_addFragment)
                }
            }
        }
    }
}
