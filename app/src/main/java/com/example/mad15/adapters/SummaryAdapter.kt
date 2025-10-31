package com.example.mad15.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mad15.activities.dashboard.HomeFragment
import com.example.mad15.databinding.ItemSummaryCardBinding

class SummaryAdapter(private val items: List<HomeFragment.SummaryItem>) :
    RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder>() {

    inner class SummaryViewHolder(val binding: ItemSummaryCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val binding = ItemSummaryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SummaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        val item = items[position]
        holder.binding.imgIcon.setImageResource(item.iconRes)
        holder.binding.tvTitle.text = item.title
        holder.binding.tvValue.text = item.value
    }

    override fun getItemCount() = items.size
}