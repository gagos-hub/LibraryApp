package com.example.libraryapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "activity_logs")
data class LogEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val action: String = "",

    val entityType: String = "",

    val entityName: String = "",

    val timestamp: Long = System.currentTimeMillis()
) {

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}