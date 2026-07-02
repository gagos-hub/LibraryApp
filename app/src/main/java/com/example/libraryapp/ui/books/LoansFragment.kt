package com.example.libraryapp.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libraryapp.R
import com.example.libraryapp.data.model.Loan
import com.example.libraryapp.data.remote.FirestoreManager
import com.example.libraryapp.databinding.FragmentLoansBinding

class LoansFragment : Fragment() {

    private var _binding: FragmentLoansBinding? = null
    private val binding get() = _binding!!

    private val firestoreManager = FirestoreManager()
    private lateinit var loanAdapter: LoanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoansBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.fabAddLoan.setOnClickListener {
            showAddLoanDialog()
        }

        fetchLoansFromCloud()
    }

    private fun setupRecyclerView() {
        loanAdapter = LoanAdapter()
        binding.recyclerViewLoans.apply {
            adapter = loanAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun fetchLoansFromCloud() {
        // Safe check πριν ξεκινήσουμε
        _binding?.progressBarLoans?.visibility = View.VISIBLE

        firestoreManager.getAllLoans { loans ->
            val currentBinding = _binding ?: return@getAllLoans

            currentBinding.progressBarLoans.visibility = View.GONE
            loanAdapter.updateLoans(loans)

            if (loans.isEmpty()) {
            }
        }
    }

    private fun showAddLoanDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_loan, null)

        AlertDialog.Builder(requireContext())
            .setTitle("Νέος Δανεισμός στο Cloud")
            .setView(dialogView)
            .setPositiveButton("Αποστολή") { _, _ ->
                val name = dialogView.findViewById<EditText>(R.id.etBorrowerName).text.toString()
                val book = dialogView.findViewById<EditText>(R.id.etLoanBookTitle).text.toString()
                val branch = dialogView.findViewById<EditText>(R.id.etLoanBranch).text.toString()

                if (name.isNotEmpty() && book.isNotEmpty()) {
                    val newLoan = Loan(
                        borrowerName = name,
                        bookTitle = book,
                        branchName = branch,
                        loanDate = "30/04/2026" // Ενημερωμένη ημερομηνία
                    )

                    firestoreManager.addLoan(newLoan,
                        onSuccess = {
                            // Χρησιμοποιούμε context? για ασφάλεια σε περίπτωση που το fragment έκλεισε
                            context?.let {
                                Toast.makeText(it, "Επιτυχής αποστολή στο Firebase!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onFailure = { e ->
                            context?.let {
                                Toast.makeText(it, "Σφάλμα: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                } else {
                    Toast.makeText(context, "Συμπληρώστε τα απαραίτητα πεδία", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Άκυρο", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Καθαρίζουμε το binding για να αποφύγουμε memory leaks
        _binding = null
    }
}