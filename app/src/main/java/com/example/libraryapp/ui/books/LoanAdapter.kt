package com.example.libraryapp.ui.books

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.R
import com.example.libraryapp.data.model.Loan

class LoanAdapter(private var loans: List<Loan> = emptyList()) :
    RecyclerView.Adapter<LoanAdapter.LoanViewHolder>() {

    class LoanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBorrower: TextView = view.findViewById(R.id.tvBorrowerName)
        val tvBook: TextView = view.findViewById(R.id.tvLoanBookTitle)
        val tvBranch: TextView = view.findViewById(R.id.tvLoanBranch)
        val tvDate: TextView = view.findViewById(R.id.tvLoanDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan, parent, false)
        return LoanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
        val loan = loans[position]
        holder.tvBorrower.text = loan.borrowerName
        holder.tvBook.text = loan.bookTitle
        holder.tvBranch.text = loan.branchName
        holder.tvDate.text = loan.loanDate
    }

    override fun getItemCount() = loans.size

    // Συνάρτηση για να ανανεώνουμε τα δεδομένα όταν έρχονται από το Firebase
    fun updateLoans(newLoans: List<Loan>) {
        loans = newLoans
        notifyDataSetChanged()
    }
}