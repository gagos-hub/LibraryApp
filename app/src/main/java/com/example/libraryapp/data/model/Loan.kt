package com.example.libraryapp.data.model

data class Loan(
    var loanId: String = "",       // Το ID του εγγράφου στη Firestore
    val bookTitle: String = "",    // Τίτλος βιβλίου που δανείστηκε
    val borrowerName: String = "", // Όνομα ατόμου (π.χ. Φοιτητής)
    val branchName: String = "",   // Από ποιο υποκατάστημα
    val loanDate: String = "",     // Ημερομηνία δανεισμού
    val status: String = "Active"  // Κατάσταση (π.χ. Active ή Returned)
)