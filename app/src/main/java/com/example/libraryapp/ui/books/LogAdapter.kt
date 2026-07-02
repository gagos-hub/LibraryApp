package com.example.libraryapp.ui.logs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.R
import com.example.libraryapp.data.model.LogEntry

class LogAdapter : ListAdapter<LogEntry, LogAdapter.LogViewHolder>(LogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = getItem(position)
        holder.bind(log)
    }

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtAction: TextView = view.findViewById(R.id.txtAction)
        private val txtEntity: TextView = view.findViewById(R.id.txtEntity)

        fun bind(log: LogEntry) {
            txtAction.text = "${log.action}: ${log.entityName}"
            txtEntity.text = "Τύπος: ${log.entityType} | ${log.timestamp}"
        }
    }

    class LogDiffCallback : DiffUtil.ItemCallback<LogEntry>() {
        override fun areItemsTheSame(oldItem: LogEntry, newItem: LogEntry) = oldItem.timestamp == newItem.timestamp
        override fun areContentsTheSame(oldItem: LogEntry, newItem: LogEntry) = oldItem == newItem
    }
}