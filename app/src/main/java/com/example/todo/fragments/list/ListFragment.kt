package com.example.todo.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todo.R
import com.example.todo.data.models.ToDoData
import com.example.todo.data.viewmodel.ToDoViewModel
import com.example.todo.databinding.FragmentListBinding
import com.example.todo.fragments.list.adapter.ListAdapter
import com.example.todo.utils.hideKeyboard
import com.example.todo.utils.observeOnce
import com.google.android.material.snackbar.Snackbar

class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val adapter: ListAdapter by lazy { ListAdapter() }
    private val viewModel: ToDoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(layoutInflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setupRecyclerView()

        viewModel.getAllData.observe(viewLifecycleOwner) {
            viewModel.checkIfDatabaseEmpty(it)
            adapter.submitList(it)
            binding.recyclerView.scheduleLayoutAnimation()
        }

        hideKeyboard(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_fragment_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@ListFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_delete_all -> deleteAllItems()
                    R.id.menu_priority_high -> viewModel.sortByHighPriority.observe(
                        viewLifecycleOwner
                    ) {
                        adapter.submitList(it)
                        binding.recyclerView.scheduleLayoutAnimation()
                    }
                    R.id.menu_priority_low -> viewModel.sortByLowPriority.observe(viewLifecycleOwner) {
                        adapter.submitList(it)
                        binding.recyclerView.scheduleLayoutAnimation()
                    }
                    android.R.id.home -> requireActivity().onBackPressed()
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun restoreDeletedItem(view: View, deletedItem: ToDoData) {
        Snackbar.make(
            view,
            "Deleted '${deletedItem.title}' successfully",
            Snackbar.LENGTH_LONG
        ).apply {
            setAction("Undo") {
                viewModel.upsert(deletedItem)
            }
            show()
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        // Swipe to delete
        swipeToDelete()
    }

    private fun swipeToDelete() {
        val itemTouchHelperCallback =
            object : SwipeToDelete() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val item = adapter.currentList[position]
                    viewModel.deleteItem(item)
                    restoreDeletedItem(viewHolder.itemView, item)

                }
            }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun deleteAllItems() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteAll()
            Snackbar.make(
                binding.root,
                "Deleted successfully: all items",
                Snackbar.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete 'All Items'")
        builder.setMessage("Are you sure to delete 'ALL ITEMS'?")

        builder.create().show()
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchThroughDatabase(newText)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        val searchQuery = "%$query%"
        viewModel.searchDatabase(searchQuery).observeOnce(viewLifecycleOwner) {
            it?.let {
                Log.d("ListFragment", "searchThroughDatabase")
                adapter.submitList(it)
            }
        }
    }

}


