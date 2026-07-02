package com.example.libraryapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.String

@Entity(tableName = "branches")
data class Branch(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val address: String,
    val phoneNumber: String
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "address" to address,
            "phoneNumber" to phoneNumber
        )
    }
}