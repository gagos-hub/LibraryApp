package com.example.libraryapp.data.remote

import com.example.libraryapp.data.model.Loan
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreManager {
    private val db = FirebaseFirestore.getInstance()
    private val loansCollection = db.collection("loans")


    fun addLoan(loan: Loan, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val newDocRef = loansCollection.document()
        val loanWithId = loan.copy(loanId = newDocRef.id)

        newDocRef.set(loanWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }


    fun getAllLoans(onResult: (List<Loan>) -> Unit) {
        loansCollection.addSnapshotListener { value, error ->
            if (error != null) return@addSnapshotListener
            val loans = value?.toObjects(Loan::class.java) ?: emptyList()
            onResult(loans)
        }
    }
}