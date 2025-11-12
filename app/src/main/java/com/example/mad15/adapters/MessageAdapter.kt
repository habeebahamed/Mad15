package com.example.mad15.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mad15.R
import com.example.mad15.data.db.entities.Message
import com.example.mad15.databinding.ItemMessageCardBinding
import com.example.mad15.databinding.ItemMessageEditBinding

class MessageAdapter(private var messageList: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(val binding: ItemMessageEditBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val msg = messageList[position]
        holder.binding.tvUsername.text = msg.username
        holder.binding.tvMessage.text = msg.message
        holder.binding.tvDate.text = msg.date

        holder.binding.ivMoreOptions.setOnClickListener { view ->
            val popup = android.widget.PopupMenu(view.context, view)
            popup.inflate(R.menu.menu_message_options)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onEditClick?.invoke(msg)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClick?.invoke(msg)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    // Add callbacks
    var onEditClick: ((Message) -> Unit)? = null
    var onDeleteClick: ((Message) -> Unit)? = null

    override fun getItemCount() = messageList.size

    fun updateData(newItems: List<Message>) {
        messageList = newItems
        notifyDataSetChanged()
    }
}