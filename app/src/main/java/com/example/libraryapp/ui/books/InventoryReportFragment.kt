package com.example.libraryapp.ui.books

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentInventoryListBinding
import com.example.libraryapp.viewmodel.LibraryViewModel

import com.example.libraryapp.ui.books.InventoryReportFragment
import com.example.libraryapp.data.model.BookStockName

class InventoryReportFragment : Fragment(R.layout.fragment_inventory_list) {

    private var _binding: FragmentInventoryListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by activityViewModels()
    private lateinit var reportAdapter: InventoryReportAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentInventoryListBinding.bind(view)

        setupRecyclerView()
        setupFilterSpinner()
    }

    private fun setupRecyclerView() {
        reportAdapter = InventoryReportAdapter(emptyList<BookStockName>())
        binding.rvInventoryReport.apply {
            adapter = reportAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupFilterSpinner() {
        viewModel.allBranches.observe(viewLifecycleOwner) { branches ->
            if (!branches.isNullOrEmpty()) {
                val branchNames = branches.map { it.name }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, branchNames)
                binding.autoCompleteFilterBranch.setAdapter(adapter)

                binding.autoCompleteFilterBranch.setOnItemClickListener { _, _, position, _ ->
                    val selectedBranch = branches[position]
                    observeReportData(selectedBranch.id)
                }
            }
        }
    }

    private fun observeReportData(branchId: Int) {
        viewModel.getStockByBranchWithJoin(branchId).observe(viewLifecycleOwner) { reportList: List<BookStockName> ->
            reportAdapter.updateData(reportList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}