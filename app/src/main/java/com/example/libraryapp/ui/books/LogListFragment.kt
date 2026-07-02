package com.example.libraryapp.ui.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.R
import com.example.libraryapp.viewmodel.LibraryViewModel

class LogListFragment : Fragment() {

    // Αρχικοποίηση του ViewModel
    private val viewModel: LibraryViewModel by viewModels()
    private lateinit var logAdapter: LogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_logs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ρύθμιση του RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.logsRecyclerView)
        logAdapter = LogAdapter() // Δημιουργία του Adapter
        recyclerView.adapter = logAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 2. Σύνδεση των Buttons από το XML
        val btnAll = view.findViewById<Button>(R.id.btnAllLogs)
        val btnBooks = view.findViewById<Button>(R.id.btnBookLogs)
        val btnRecent = view.findViewById<Button>(R.id.btnRecentLogs)
        val btnDeleted = view.findViewById<Button>(R.id.btnDeletedBranches)

        // 3. Click Listeners για την ενεργοποίηση των Firestore Queries
        btnAll.setOnClickListener { viewModel.setLogFilter(0) }     // Τοπικά Logs
        btnBooks.setOnClickListener { viewModel.setLogFilter(1) }   // Firestore WHERE
        btnRecent.setOnClickListener { viewModel.setLogFilter(2) }  // Firestore LIMIT/ORDER BY
        btnDeleted.setOnClickListener { viewModel.setLogFilter(3) } // Firestore COMPOSITE

        // 4. Παρακολούθηση (Observation) των δεδομένων
        // Κάθε φορά που πατάς κουμπί, το ViewModel αλλάζει τα δεδομένα και το UI ενημερώνεται αυτόματα
        viewModel.displayLogs.observe(viewLifecycleOwner) { logs ->
            logAdapter.submitList(logs)
        }
    }
}