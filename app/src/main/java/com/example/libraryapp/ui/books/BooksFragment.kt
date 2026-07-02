package com.example.libraryapp.ui.books

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.data.model.Book
import com.example.libraryapp.databinding.FragmentBooksBinding
import com.example.libraryapp.viewmodel.LibraryViewModel
import com.google.android.material.snackbar.Snackbar
// ΠΡΟΣΘΗΚΗ: Το import του helper που φτιάξαμε
import com.example.libraryapp.util.NotificationHelper

class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by activityViewModels()
    private lateinit var adapter: BookAdapter

    private lateinit var notificationHelper: NotificationHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Αρχικοποίηση του helper
        notificationHelper = NotificationHelper(requireContext())

        setupRecyclerView()
        setupSwipeToDelete()

        viewModel.allBooks.observe(viewLifecycleOwner) { books ->
            adapter.setData(books)
        }

        viewModel.booksCount.observe(viewLifecycleOwner) { count ->
            binding.tvTotalBooks.text = "Σύνολο βιβλίων στη βάση: $count"
        }

        binding.fabAddBook.setOnClickListener {
            showAddBookDialog()
        }

        binding.etSearchBook.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    viewModel.searchBooks(query).observe(viewLifecycleOwner) { filteredList ->
                        adapter.setData(filteredList)
                    }
                } else {
                    viewModel.allBooks.observe(viewLifecycleOwner) { books ->
                        adapter.setData(books)
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = BookAdapter { book ->
            showDeleteConfirmation(book)
        }
        binding.recyclerViewBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBooks.adapter = adapter
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val bookToDelete = adapter.getBookAt(position)

                viewModel.delete(bookToDelete)

                Snackbar.make(binding.root, "Το βιβλίο '${bookToDelete.title}' διαγράφηκε", Snackbar.LENGTH_LONG)
                    .setAction("ΑΚΥΡΩΣΗ") {
                        viewModel.insert(bookToDelete)
                    }.show()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerViewBooks)
    }

    private fun showDeleteConfirmation(book: Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Διαγραφή")
            .setMessage("Θέλετε να διαγράψετε το βιβλίο '${book.title}';")
            .setPositiveButton("Ναι") { _, _ ->
                viewModel.delete(book)
                Snackbar.make(binding.root, "Το βιβλίο διαγράφηκε", Snackbar.LENGTH_LONG)
                    .setAction("ΑΚΥΡΩΣΗ") { viewModel.insert(book) }.show()
            }
            .setNegativeButton("Όχι", null)
            .show()
    }

    private fun showAddBookDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Προσθήκη Νέου Βιβλίου")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(60, 40, 60, 10)

        val etTitle = EditText(requireContext()).apply { hint = "Τίτλος Βιβλίου" }
        val etAuthor = EditText(requireContext()).apply { hint = "Συγγραφέας" }

        layout.addView(etTitle)
        layout.addView(etAuthor)
        builder.setView(layout)

        builder.setPositiveButton("Προσθήκη") { _, _ ->
            val title = etTitle.text.toString()
            val author = etAuthor.text.toString()

            if (title.isNotEmpty() && author.isNotEmpty()) {
                val newBook = Book(title = title, author = author)
                viewModel.insert(newBook)

                // ΠΡΟΣΘΗΚΗ: Κλήση της ειδοποίησης
                notificationHelper.sendBookAddedNotification(title)

                Toast.makeText(context, "Το βιβλίο προστέθηκε!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Ακύρωση", null)
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}