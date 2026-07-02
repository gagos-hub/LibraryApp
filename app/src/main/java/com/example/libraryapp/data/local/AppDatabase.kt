package com.example.libraryapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.libraryapp.data.model.Book
import com.example.libraryapp.data.model.Branch
import com.example.libraryapp.data.model.Inventory
import com.example.libraryapp.data.model.LogEntry

// Η έκδοση (version) αυξήθηκε στο 3 για να συμπεριλάβει το LogEntry και το Inventory
@Database(
    entities = [Book::class, Branch::class, Inventory::class, LogEntry::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun libraryDao(): LibraryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "library_database"
                )
                    // Προσθήκη για αυτόματη διαγραφή και αναδημιουργία της βάσης σε αλλαγές
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}