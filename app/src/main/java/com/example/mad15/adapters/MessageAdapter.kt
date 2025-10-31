package com.example.mad15.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mad15.R
import com.example.mad15.data.db.entities.Message
import com.example.mad15.databinding.ItemMessageBinding

class MessageAdapter(private val messageList: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val msg = messageList[position]
        holder.binding.tvUsername.text = msg.username
        holder.binding.tvMessage.text = msg.message
        holder.binding.tvDate.text = msg.date
    }

    override fun getItemCount() = messageList.size
}