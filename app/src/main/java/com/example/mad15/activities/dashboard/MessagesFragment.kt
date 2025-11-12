package com.example.mad15.activities.dashboard

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mad15.R
import com.example.mad15.adapters.MessageAdapter
import com.example.mad15.data.db.entities.Message
import com.example.mad15.databinding.FragmentMessagesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date
import java.util.Locale

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MessageAdapter
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)

        adapter = MessageAdapter(emptyList())
        binding.recyclerMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMessages.adapter = adapter


        // Firestore listener (Read)
        listenerRegistration = db.collection("messages")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val items = snapshot.documents.mapNotNull { it.toObject(Message::class.java).apply {
                    // include document ID for edit/delete
                    this?.let { it.copy() }
                } }
                adapter.updateData(items)
            }

        // Handle adapter menu callbacks
        adapter.onEditClick = { msg -> showAddEditMessageDialog(msg) }
        adapter.onDeleteClick = { msg -> deleteMessage(msg) }

        // seedMessages() // <- run once if needed, then remove

        // Handle toolbar menu click
        binding.messagesToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_add_message -> {
                    showAddEditMessageDialog()
                    true
                }
                else -> false
            }
        }

        return binding.root
    }

    fun showAddEditMessageDialog(existingMsg: Message? = null) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_message, null)
        val etUsername = dialogView.findViewById<EditText>(R.id.etUsername)
        val etMessage = dialogView.findViewById<EditText>(R.id.etMessage)

        if (existingMsg != null) {
            etUsername.setText(existingMsg.username)
            etMessage.setText(existingMsg.message)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (existingMsg == null) "Add Message" else "Edit Message")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val username = etUsername.text.toString().trim()
                val message = etMessage.text.toString().trim()
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                if (username.isNotEmpty() && message.isNotEmpty()) {
                    val newMsg = Message(username, message, date)
                    if (existingMsg == null) {
                        db.collection("messages").add(newMsg)
                    } else {
                        db.collection("messages")
                            .whereEqualTo("username", existingMsg.username)
                            .whereEqualTo("message", existingMsg.message)
                            .get()
                            .addOnSuccessListener { docs ->
                                for (doc in docs) {
                                    db.collection("messages").document(doc.id).set(newMsg)
                                }
                            }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteMessage(msg: Message) {
        db.collection("messages")
            .whereEqualTo("username", msg.username)
            .whereEqualTo("message", msg.message)
            .get()
            .addOnSuccessListener { docs ->
                for (doc in docs) {
                    db.collection("messages").document(doc.id).delete()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
        _binding = null
    }

    private fun seedMessages() {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val seed = listOf(
            Message("Sarah Chen", "Hey everyone! Just finished the project documentation. Please review it when you get a chance.", "2024-10-31"),
            Message("Mike Rodriguez", "Great work on the presentation today! The client seemed really impressed with our progress.", "2024-10-30"),
            Message("Emily Watson", "Reminder: Team meeting scheduled for tomorrow at 10 AM. Don't forget to bring your status updates!", "2024-10-29"),
            Message("James Park", "I've pushed the latest changes to the dev branch. Let me know if you encounter any issues.", "2024-10-28"),
            Message("Olivia Martinez", "Happy birthday Alex! Hope you have an amazing day! 🎉", "2024-10-27"),
            Message("David Kim", "Can someone help me debug this issue? I've been stuck on it for hours.", "2024-10-26"),
            Message("Rachel Foster", "The new design mockups are ready for review. Check them out in the shared folder.", "2024-10-25"),
            Message("Alex Thompson", "Thanks everyone for the birthday wishes! You guys are awesome!", "2024-10-24"),
            Message("Nina Patel", "Just deployed the hotfix to production. Everything should be working smoothly now.", "2024-10-23"),
            Message("Chris Anderson", "Weekend plans anyone? Thinking of organizing a team lunch next Friday.", "2024-10-22")
        )

        val col = db.collection("messages")
        seed.forEach { col.add(it) }
    }
}