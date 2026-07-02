package com.example.libraryapp.ui.branches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.data.model.Branch
import com.example.libraryapp.databinding.ItemBranchBinding

class BranchAdapter : RecyclerView.Adapter<BranchAdapter.BranchViewHolder>() {

    private var branches = emptyList<Branch>()

    class BranchViewHolder(val binding: ItemBranchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder {
        // Χρήση View Binding για το φούσκωμα του layout
        val binding = ItemBranchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BranchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
        val current = branches[position]

        holder.binding.tvBranchName.text = current.name

        holder.binding.tvBranchLocation.text = current.address

    }

    override fun getItemCount() = branches.size

    fun setData(newBranches: List<Branch>) {
        this.branches = newBranches
        notifyDataSetChanged()
    }

    fun getBranchAt(position: Int): Branch = branches[position]
}