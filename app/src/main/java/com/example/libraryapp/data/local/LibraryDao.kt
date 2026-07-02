package com.example.libraryapp.data.local

import androidx.room.*
import com.example.libraryapp.data.model.Book
import com.example.libraryapp.data.model.BookStockName
import com.example.libraryapp.data.model.Branch
import com.example.libraryapp.data.model.LogEntry
import kotlinx.coroutines.flow.Flow
import com.example.libraryapp.data.model.Inventory

@Dao
interface LibraryDao {

    // --- ΒΙΒΛΙΑ (Queries) ---

    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE title LIKE :searchQuery")
    fun searchBooks(searchQuery: String): Flow<List<Book>>

    @Query("SELECT COUNT(*) FROM books")
    fun getBooksCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book) // Μετονομασία σε insert για ευκολία

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()

    // --- ΥΠΟΚΑΤΑΣΤΗΜΑΤΑ (Queries) ---

    @Query("SELECT * FROM branches")
    fun getAllBranches(): Flow<List<Branch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: Branch)

    @Update
    suspend fun updateBranch(branch: Branch)

    @Delete
    suspend fun deleteBranch(branch: Branch)


    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<LogEntry>>

    @Insert
    suspend fun insertLog(log: LogEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: Inventory)

    @Query("SELECT * FROM inventory WHERE branchId = :bId")
    fun getInventoryByBranch(bId: Int): List<Inventory>

    @Query("""
    SELECT books.title as title, inventory.quantity as stock 
    FROM inventory 
    INNER JOIN books ON inventory.bookId = books.Id 
    WHERE inventory.branchId = :bId
""")
    fun getStockWithTitles(bId: Int): Flow<List<BookStockName>>
}
