package com.example.libraryapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory",
    foreignKeys = [
        ForeignKey(
            entity = Branch::class,
            parentColumns = ["id"],
            childColumns = ["branchId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("branchId"), Index("bookId")]
)
data class Inventory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val branchId: Int,
    val branchName: String,
    val bookId: Int,
    val bookTitle: String,
    val quantity: Int
) {
    // Αυτή η συνάρτηση προετοιμάζει τα δεδομένα για τη Firestore
    fun toMap(): Map<String, Any> {
        return mapOf(
            "branchId" to branchId,
            "branchName" to branchName,
            "bookId" to bookId,
            "bookTitle" to bookTitle,
            "quantity" to quantity,
            "timestamp" to System.currentTimeMillis()
        )
    }
}