package com.example.todo.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.data.models.ToDoData
import com.example.todo.data.viewmodel.ToDoViewModel
import com.example.todo.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private val viewModel: ToDoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(layoutInflater, container, false)

        binding.currentPrioritiesSpinner.onItemSelectedListener = viewModel.listener

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_add -> insertDataToDB()
                    android.R.id.home -> requireActivity().onBackPressed()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun insertDataToDB() {
        val mTitle = binding.currentTitleEt.text.toString()
        val mPriority = binding.currentPrioritiesSpinner.selectedItem.toString()
        val mDescription = binding.descriptionEt.text.toString()

        val validation = viewModel.verifyDataFromUser(mTitle, mDescription)
        if (validation) {
            val newData = ToDoData(
                id = 0,
                title = mTitle,
                priority = viewModel.parsePriority(mPriority),
                description = mDescription
            )
            viewModel.upsert(newData)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT)
                .show()
        }
    }


}