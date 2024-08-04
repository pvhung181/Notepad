package com.lutech.notepad.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.lutech.notepad.R
import com.lutech.notepad.adapter.CategoryAdapter
import com.lutech.notepad.adapter.TaskAdapter
import com.lutech.notepad.databinding.FragmentCategoryBinding
import com.lutech.notepad.model.Category
import com.lutech.notepad.ui.home.HomeViewModel
import java.util.Collections

class CategoryFragment : Fragment(), MenuProvider {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryViewModel: CategoryViewModel

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private var draggedItemIndex: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()
        setupRecyclerView()
        setListeners()
        setupDragItem()
        return root
    }

    private fun init() {
        categoryViewModel =
            ViewModelProvider(this)[CategoryViewModel::class.java]
    }

    private fun setupRecyclerView() {
        recycler = binding.recyclerCategory
        adapter = CategoryAdapter(activity = requireActivity())
        recycler.adapter = adapter
        categoryViewModel.categories.observe(viewLifecycleOwner) {
            adapter.setCategory(it)
            categoryViewModel.categoryCheck = it
            binding.addBtn.isEnabled = true
        }
    }

    private fun setupDragItem() {
        val itemTouchHelper: ItemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.Callback(){
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    return makeMovementFlags(
                        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                        0
                    )
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    draggedItemIndex = viewHolder.bindingAdapterPosition
                    val targetIndex = target.bindingAdapterPosition

                    Collections.swap(adapter.categories, draggedItemIndex!!, targetIndex)
                    adapter.notifyItemMoved(draggedItemIndex!!, targetIndex)

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                }

            }
        )

        itemTouchHelper.attachToRecyclerView(recycler)
    }

    private fun setListeners() {
        binding.addBtn.setOnClickListener {
            categoryViewModel.insertCategory(Category(categoryName = binding.categoryEditText.text.toString()))
            //binding.categoryEditText.text.clear()
            binding.categoryEditText.requestFocus()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.addMenuProvider(this, viewLifecycleOwner)
        activity?.setTitle(R.string.categories)
        (activity as AppCompatActivity).supportActionBar?.subtitle = null

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }

}