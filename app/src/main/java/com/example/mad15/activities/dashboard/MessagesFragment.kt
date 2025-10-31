package com.example.mad15.activities.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mad15.adapters.MessageAdapter
import com.example.mad15.data.db.entities.Message
import com.example.mad15.databinding.FragmentMessagesBinding
import org.json.JSONArray

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)

        // Hardcoded JSON string
        val jsonString = """
            [
              {"username": "Sarah Chen", "message": "Hey everyone! Just finished the project documentation. Please review it when you get a chance.", "date": "2024-10-31"},
              {"username": "Mike Rodriguez", "message": "Great work on the presentation today! The client seemed really impressed with our progress.", "date": "2024-10-30"},
              {"username": "Emily Watson", "message": "Reminder: Team meeting scheduled for tomorrow at 10 AM. Don't forget to bring your status updates!", "date": "2024-10-29"},
              {"username": "James Park", "message": "I've pushed the latest changes to the dev branch. Let me know if you encounter any issues.", "date": "2024-10-28"},
              {"username": "Olivia Martinez", "message": "Happy birthday Alex! Hope you have an amazing day! 🎉", "date": "2024-10-27"},
              {"username": "David Kim", "message": "Can someone help me debug this issue? I've been stuck on it for hours.", "date": "2024-10-26"},
              {"username": "Rachel Foster", "message": "The new design mockups are ready for review. Check them out in the shared folder.", "date": "2024-10-25"},
              {"username": "Alex Thompson", "message": "Thanks everyone for the birthday wishes! You guys are awesome!", "date": "2024-10-24"},
              {"username": "Nina Patel", "message": "Just deployed the hotfix to production. Everything should be working smoothly now.", "date": "2024-10-23"},
              {"username": "Chris Anderson", "message": "Weekend plans anyone? Thinking of organizing a team lunch next Friday.", "date": "2024-10-22"}
            ]
        """.trimIndent()

        val messages = parseMessagesFromJson(jsonString)

        binding.recyclerMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMessages.adapter = MessageAdapter(messages)

        return binding.root
    }

    private fun parseMessagesFromJson(jsonString: String): List<Message> {
        val jsonArray = JSONArray(jsonString)
        val messages = mutableListOf<Message>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            messages.add(
                Message(
                    username = obj.getString("username"),
                    message = obj.getString("message"),
                    date = obj.getString("date")
                )
            )
        }
        return messages
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}