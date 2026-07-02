package com.example.libraryapp.ui.books

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.data.model.Branch
import com.example.libraryapp.databinding.FragmentBranchesBinding
import com.example.libraryapp.ui.branches.BranchAdapter
import com.example.libraryapp.viewmodel.LibraryViewModel
import com.google.android.material.snackbar.Snackbar

class BranchesFragment : Fragment() {

    private var _binding: FragmentBranchesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by activityViewModels()
    private lateinit var branchAdapter: BranchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBranchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.allBranches.observe(viewLifecycleOwner) { branches ->
            branches?.let {
                branchAdapter.setData(it)
            }
        }

        binding.fabAddBranch.setOnClickListener {
            showAddBranchDialog()
        }
    }

    private fun setupRecyclerView() {
        branchAdapter = BranchAdapter()
        binding.recyclerViewBranches.apply {
            adapter = branchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val branchToDelete = branchAdapter.getBranchAt(position) // Βεβαιώσου ότι έχεις αυτή τη μέθοδο στον Adapter

                // Διαγραφή
                viewModel.deleteBranch(branchToDelete)

                // Εμφάνιση Snackbar με δυνατότητα αναίρεσης
                Snackbar.make(binding.root, "Το υποκατάστημα διαγράφηκε", Snackbar.LENGTH_LONG)
                    .setAction("ΑΚΥΡΩΣΗ") {
                        viewModel.insertBranch(branchToDelete)
                    }.show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerViewBranches)
    }

    private fun showAddBranchDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Προσθήκη Υποκαταστήματος")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(60, 40, 60, 10)

        val etName = EditText(requireContext()).apply { hint = "Όνομα Υποκαταστήματος" }
        val etAddress = EditText(requireContext()).apply { hint = "Διεύθυνση" }
        val etPhone = EditText(requireContext()).apply { hint = "Τηλέφωνο" }

        layout.addView(etName)
        layout.addView(etAddress)
        layout.addView(etPhone)

        builder.setView(layout)

        builder.setPositiveButton("Αποθήκευση") { _, _ ->
            val name = etName.text.toString()
            if (name.isNotEmpty()) {
                val newBranch = Branch(
                    id = 0,
                    name = name,
                    address = etAddress.text.toString(),
                    phoneNumber = etPhone.text.toString()
                )
                viewModel.insertBranch(newBranch)
            } else {
                Toast.makeText(context, "Το όνομα είναι υποχρεωτικό", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Ακύρωση", null)
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}