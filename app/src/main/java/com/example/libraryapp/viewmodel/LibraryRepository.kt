package com.example.libraryapp.viewmodel // Διόρθωση του package στο σωστό φάκελο

import com.example.libraryapp.data.local.LibraryDao
import com.example.libraryapp.data.model.Book
import com.example.libraryapp.data.model.Branch
import com.example.libraryapp.data.model.Inventory
import com.example.libraryapp.data.model.LogEntry
import com.example.libraryapp.data.model.BookStockName
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class LibraryRepository(private val libraryDao: LibraryDao) {

    // Αρχικοποίηση Firestore
    private val firestore = FirebaseFirestore.getInstance()

    // --- LOGS ---
    val allLogs: Flow<List<LogEntry>> = libraryDao.getAllLogs()

    private suspend fun saveLog(action: String, type: String, name: String) {
        val log = LogEntry(
            action = action,
            entityType = type,
            entityName = name
        )
        libraryDao.insertLog(log)
        firestore.collection("logs").add(log)
    }

    // --- ΒΙΒΛΙΑ ---
    val allBooks: Flow<List<Book>> = libraryDao.getAllBooks()

    suspend fun insert(book: Book) {
        libraryDao.insert(book)
        firestore.collection("books")
            .document(book.id.toString())
            .set(book.toMap())
        saveLog("ΠΡΟΣΘΗΚΗ", "ΒΙΒΛΙΟ", book.title)
    }

    suspend fun deleteBook(book: Book) {
        libraryDao.deleteBook(book)
        firestore.collection("books")
            .document(book.id.toString())
            .delete()
        saveLog("ΔΙΑΓΡΑΦΗ", "ΒΙΒΛΙΟ", book.title)
    }

    suspend fun updateBook(book: Book) {
        libraryDao.updateBook(book)
        firestore.collection("books")
            .document(book.id.toString())
            .update(book.toMap())
        saveLog("ΕΝΗΜΕΡΩΣΗ", "ΒΙΒΛΙΟ", book.title)
    }

    fun searchBooks(query: String): Flow<List<Book>> {
        return libraryDao.searchBooks("%$query%") // Προσθήκη % για το LIKE query
    }

    fun getBooksCount(): Flow<Int> {
        return libraryDao.getBooksCount()
    }

    suspend fun deleteAllBooks() {
        libraryDao.deleteAllBooks()
        saveLog("ΜΑΖΙΚΗ ΔΙΑΓΡΑΦΗ", "ΒΙΒΛΙΑ", "Όλα τα βιβλία")
    }

    // --- ΥΠΟΚΑΤΑΣΤΗΜΑΤΑ ---
    val allBranches: Flow<List<Branch>> = libraryDao.getAllBranches()

    suspend fun insertBranch(branch: Branch) {
        libraryDao.insertBranch(branch)
        firestore.collection("branches")
            .document(branch.id.toString())
            .set(branch.toMap())
        saveLog("ΠΡΟΣΘΗΚΗ", "ΥΠΟΚΑΤΑΣΤΗΜΑ", branch.name)
    }

    suspend fun deleteBranch(branch: Branch) {
        libraryDao.deleteBranch(branch)
        firestore.collection("branches")
            .document(branch.id.toString())
            .delete()
        saveLog("ΔΙΑΓΡΑΦΗ", "ΥΠΟΚΑΤΑΣΤΗΜΑ", branch.name)
    }

    suspend fun updateBranch(branch: Branch) {
        libraryDao.updateBranch(branch)
        firestore.collection("branches")
            .document(branch.id.toString())
            .update(branch.toMap())
        saveLog("ΕΝΗΜΕΡΩΣΗ", "ΥΠΟΚΑΤΑΣΤΗΜΑ", branch.name)
    }

    // --- ΑΠΟΘΕΜΑ (INVENTORY) ---
    suspend fun insertInventory(inventory: Inventory) {
        // 1. Τοπική αποθήκευση στη Room
        libraryDao.insertInventory(inventory)

        // 2. Συγχρονισμός με Firestore (χρησιμοποιώντας το toMap() για NoSQL δομή)
        firestore.collection("inventory")
            .add(inventory.toMap())

        // 3. Καταγραφή στο Log
        saveLog("ΚΑΤΑΧΩΡΗΣΗ", "ΑΠΟΘΕΜΑ", "${inventory.bookTitle} στο ${inventory.branchName}")
    }

    // --- ΕΡΩΤΗΜΑΤΑ JOIN (Ερώτημα 3) ---
    fun getStockWithTitles(branchId: Int): Flow<List<BookStockName>> {
        return libraryDao.getStockWithTitles(branchId)
    }



    // 1. Ερώτημα: Φιλτράρισμα μόνο για Logs τύπου "ΒΙΒΛΙΟ" (WHERE)
    fun getFirestoreBookLogs(): Flow<List<LogEntry>> = callbackFlow {
        val subscription = firestore.collection("logs")
            .whereEqualTo("entityType", "ΒΙΒΛΙΟ")
            .addSnapshotListener { snapshot, _ ->
                val logs = snapshot?.toObjects(LogEntry::class.java) ?: emptyList()
                trySend(logs)
            }
        awaitClose { subscription.remove() }
    }

    // 2. Ερώτημα: Τα 10 πιο πρόσφατα Logs (ORDER BY & LIMIT)
    fun getRecentFirestoreLogs(): Flow<List<LogEntry>> = callbackFlow {
        val subscription = firestore.collection("logs")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, _ ->
                val logs = snapshot?.toObjects(LogEntry::class.java) ?: emptyList()
                trySend(logs)
            }
        awaitClose { subscription.remove() }
    }

    // 3. Ερώτημα: Σύνθετο Φίλτρο - "ΔΙΑΓΡΑΦΕΣ" σε "ΥΠΟΚΑΤΑΣΤΗΜΑΤΑ" (Multiple WHERE)
    fun getDeletedBranchesFirestore(): Flow<List<LogEntry>> = callbackFlow {
        val subscription = firestore.collection("logs")
            .whereEqualTo("action", "ΔΙΑΓΡΑΦΗ")
            .whereEqualTo("entityType", "ΥΠΟΚΑΤΑΣΤΗΜΑ")
            .addSnapshotListener { snapshot, _ ->
                val logs = snapshot?.toObjects(LogEntry::class.java) ?: emptyList()
                trySend(logs)
            }
        awaitClose { subscription.remove() }
    }
}