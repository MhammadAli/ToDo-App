package com.example.todo.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.R
import com.example.todo.data.models.ToDoData
import com.example.todo.data.viewmodel.ToDoViewModel
import com.example.todo.databinding.FragmentUpdateBinding
import com.google.android.material.snackbar.Snackbar


class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ToDoViewModel by viewModels()
    private val args by navArgs<UpdateFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(layoutInflater, container, false)

        binding.args = args

        binding.currentPrioritiesSpinner.onItemSelectedListener = viewModel.listener

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.update_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_save -> updateItem()
                    R.id.menu_delete -> deleteItem()
                    android.R.id.home ->requireActivity().onBackPressed()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun deleteItem() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteItem(args.currentItem)
            Snackbar.make(
                binding.root,
                "Deleted successfully: ${args.currentItem.title}",
                Snackbar.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete '${args.currentItem.title}'")
        builder.setMessage("Are you sure to delete '${args.currentItem.title}'?")

        builder.create().show()
    }

    private fun updateItem() {
        val title = binding.currentTitleEt.text.toString()
        val description = binding.currentDescriptionEt.text.toString()
        val priority = binding.currentPrioritiesSpinner.selectedItem.toString()
        val validation = viewModel.verifyDataFromUser(title = title, description = description)
        if (validation) {
            val updatedItem =
                ToDoData(
                    id = args.currentItem.id,
                    title = title,
                    description = description,
                    priority = viewModel.parsePriority(priority)
                )
            viewModel.upsert(updatedItem)
            Toast.makeText(requireContext(), "Successfully updated", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT)
                .show()
        }
    }

}