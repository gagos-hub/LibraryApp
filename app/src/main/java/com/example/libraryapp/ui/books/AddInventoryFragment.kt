package com.example.libraryapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.libraryapp.data.model.Inventory
import com.example.libraryapp.databinding.FragmentAddInventoryBinding
import com.example.libraryapp.viewmodel.LibraryViewModel

class AddInventoryFragment : Fragment() {

    private var _binding: FragmentAddInventoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()

        binding.btnSaveInventory.setOnClickListener {
            saveInventoryEntry()
        }
    }

    private fun setupSpinners() {
        viewModel.allBooks.observe(viewLifecycleOwner) { books ->
            if (!books.isNullOrEmpty()) {
                val bookTitles = books.map { it.title }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, bookTitles)
                binding.spinnerBooks.setAdapter(adapter)
            }
        }

        viewModel.allBranches.observe(viewLifecycleOwner) { branches ->
            if (!branches.isNullOrEmpty()) {
                val branchNames = branches.map { it.name }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, branchNames)
                // ΔΙΟΡΘΩΣΗ: setAdapter() αντί για .adapter
                binding.spinnerBranches.setAdapter(adapter)
            }
        }
    }

    private fun saveInventoryEntry() {
        val booksList = viewModel.allBooks.value
        val branchesList = viewModel.allBranches.value
        val quantityText = binding.etQuantity.text.toString()

        if (booksList.isNullOrEmpty() || branchesList.isNullOrEmpty()) {
            Toast.makeText(context, "Προσθέστε πρώτα βιβλία και υποκαταστήματα!", Toast.LENGTH_LONG).show()
            return
        }

        if (quantityText.isEmpty()) {
            binding.etQuantity.error = "Παρακαλώ εισάγετε ποσότητα"
            return
        }

        val selectedBookTitle = binding.spinnerBooks.text.toString()
        val selectedBranchName = binding.spinnerBranches.text.toString()

        val selectedBook = booksList.find { it.title == selectedBookTitle }
        val selectedBranch = branchesList.find { it.name == selectedBranchName }

        if (selectedBook == null || selectedBranch == null) {
            Toast.makeText(context, "Παρακαλώ επιλέξτε βιβλίο και υποκατάστημα από τη λίστα", Toast.LENGTH_SHORT).show()
            return
        }

        val quantity = quantityText.toInt()

        val inventory = Inventory(
            bookId = selectedBook.id,
            bookTitle = selectedBook.title,
            branchId = selectedBranch.id,
            branchName = selectedBranch.name,
            quantity = quantity
        )

        viewModel.insertInventory(inventory)

        Toast.makeText(context, "Το απόθεμα ενημερώθηκε επιτυχώς!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}