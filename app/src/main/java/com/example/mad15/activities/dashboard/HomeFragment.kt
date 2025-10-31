package com.example.mad15.activities.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mad15.R
import com.example.mad15.adapters.SummaryAdapter
import com.example.mad15.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    data class SummaryItem(val iconRes: Int, val title: String, val value: String)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupGreeting()
        setupSummaryGrid()
        setupQuickActions()
        setupMotivationCard()

        return binding.root
    }

    private fun setupGreeting() {
        val userName = "Habeeb" // Later fetch from DB
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            else -> "Good Evening"
        }
        binding.tvGreeting.text = "$greeting, $userName!"
        binding.tvSubGreeting.text = "Let's achieve something amazing today."
    }

    private fun setupSummaryGrid() {
        val items = listOf(
            SummaryItem(R.drawable.ic_messages, "Messages", "10"),
            SummaryItem(R.drawable.ic_task, "Tasks", "5"),
            SummaryItem(R.drawable.ic_account, "Contacts", "12"),
            SummaryItem(R.drawable.ic_event, "Events", "3")
        )

        val adapter = SummaryAdapter(items)
        binding.recyclerSummary.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerSummary.adapter = adapter
    }

    private fun setupQuickActions() {
        val actions = listOf("Add Task", "View Calendar", "Profile Settings", "Backup DB")
        val context = requireContext()

        for (action in actions) {
            val chip = TextView(context).apply {
                text = action
                setPadding(32, 16, 32, 16)
                setBackgroundResource(R.drawable.bg_chip)
                setTextColor(context.getColor(R.color.teal_700))
                textSize = 14f
                setOnClickListener {
                    // TODO: handle each action
                }
            }
            binding.layoutQuickActions.addView(chip)
        }
    }

    private fun setupMotivationCard() {
        val quotes = listOf(
            "✨ Consistency turns average into excellence.",
            "🚀 Small progress every day leads to big results.",
            "💡 Focus on being productive, not busy."
        )
        binding.tvMotivation.text = quotes.random()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}