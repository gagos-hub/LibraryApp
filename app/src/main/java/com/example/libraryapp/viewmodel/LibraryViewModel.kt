package com.example.libraryapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.libraryapp.data.local.AppDatabase
import com.example.libraryapp.data.model.Book
import com.example.libraryapp.data.model.Branch
import com.example.libraryapp.data.model.Inventory
import com.example.libraryapp.data.model.LogEntry
import com.example.libraryapp.data.model.BookStockName
import com.example.libraryapp.viewmodel.LibraryRepository
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LibraryRepository

    val allBooks: LiveData<List<Book>>
    val allBranches: LiveData<List<Branch>>
    val booksCount: LiveData<Int>

    // LiveData για τον έλεγχο των φίλτρων Firestore
    // 0: Τοπικά Logs, 1: Firestore Book Logs, 2: Recent Firestore Logs, 3: Deleted Branches
    private val _queryType = MutableLiveData<Int>(0)

    init {
        val dao = AppDatabase.getDatabase(application).libraryDao()
        repository = LibraryRepository(dao)

        allBooks = repository.allBooks.asLiveData()
        allBranches = repository.allBranches.asLiveData()
        booksCount = repository.getBooksCount().asLiveData()
    }

    // --- FIRESTORE QUERIES (ΤΑ 3 ΕΡΩΤΗΜΑΤΑ) ---

    // Χρησιμοποιούμε switchMap για να αλλάζουμε το Flow που παρακολουθούμε
    val displayLogs: LiveData<List<LogEntry>> = _queryType.switchMap { type ->
        when (type) {
            1 -> repository.getFirestoreBookLogs().asLiveData()       // Ερώτημα 1: WHERE
            2 -> repository.getRecentFirestoreLogs().asLiveData()     // Ερώτημα 2: ORDER BY & LIMIT
            3 -> repository.getDeletedBranchesFirestore().asLiveData() // Ερώτημα 3: COMPOSITE WHERE
            else -> repository.allLogs.asLiveData()                   // Default: Τοπικά Logs (Room)
        }
    }

    fun setLogFilter(filterType: Int) {
        _queryType.value = filterType
    }

    // --- ΛΕΙΤΟΥΡΓΙΕΣ ΒΙΒΛΙΩΝ (ROOM & FIRESTORE) ---

    fun insert(book: Book) = viewModelScope.launch {
        repository.insert(book)
    }

    fun delete(book: Book) = viewModelScope.launch {
        repository.deleteBook(book)
    }

    fun updateBook(book: Book) = viewModelScope.launch {
        repository.updateBook(book)
    }

    fun searchBooks(query: String): LiveData<List<Book>> {
        return repository.searchBooks(query).asLiveData()
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAllBooks()
    }

    // --- ΛΕΙΤΟΥΡΓΙΕΣ ΥΠΟΚΑΤΑΣΤΗΜΑΤΩΝ ---

    fun insertBranch(branch: Branch) = viewModelScope.launch {
        repository.insertBranch(branch)
    }

    fun deleteBranch(branch: Branch) = viewModelScope.launch {
        repository.deleteBranch(branch)
    }

    fun updateBranch(branch: Branch) = viewModelScope.launch {
        repository.updateBranch(branch)
    }

    // --- INVENTORY & JOIN (SQL QUERY) ---

    fun insertInventory(inventory: Inventory) = viewModelScope.launch {
        repository.insertInventory(inventory)
    }

    fun getStockByBranchWithJoin(branchId: Int): LiveData<List<BookStockName>> {
        return repository.getStockWithTitles(branchId).asLiveData()
    }
}