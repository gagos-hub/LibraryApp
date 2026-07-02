package com.example.libraryapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.Int



    @Entity(tableName = "books")
    data class Book(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val title: String,
        val author: String,
        val isbn: String ="" // Ο διεθνής κωδικός
    ) {
        fun toMap(): Map<String, Any?> {
            return mapOf(
                "title" to title,
                "author" to author
            )
        }
    }