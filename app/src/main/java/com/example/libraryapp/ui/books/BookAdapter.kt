package com.example.libraryapp.ui.books

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.data.model.Book
import com.example.libraryapp.databinding.ItemBookBinding // Βεβαιώσου ότι αυτό το import είναι σωστό

class BookAdapter(private val onDeleteClick: (Book) -> Unit) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private var booksList = emptyList<Book>()

    fun setData(newBooks: List<Book>) {
        this.booksList = newBooks
        notifyDataSetChanged()
    }

    fun getBookAt(position: Int): Book {
        return booksList[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentBook = booksList[position]

        holder.binding.tvBookTitle.text = currentBook.title
        holder.binding.tvBookAuthor.text = currentBook.author

        // Διαγραφή με κλικ
        holder.itemView.setOnClickListener { onDeleteClick(currentBook) }
    }

    override fun getItemCount(): Int = booksList.size

    class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)
}