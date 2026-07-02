package com.example.libraryapp.ui.books

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.R
import com.example.libraryapp.data.model.BookStockName

class InventoryReportAdapter(private var items: List<BookStockName>) :
    RecyclerView.Adapter<InventoryReportAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvBookTitle)
        val stock: TextView = view.findViewById(R.id.tvStockCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.stock.text = "Απόθεμα: ${item.stock}"
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<BookStockName>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}