package com.example.auvan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.auvan.databinding.ItemNotificationBinding
import com.example.auvan.databinding.ItemNotificationHeaderBinding

sealed class NotificationItem {
    data class Header(val title: String) : NotificationItem()
    data class Entry(val notification: Notification, val isNew: Boolean = false) : NotificationItem()
}

class NotificationAdapter(
    private val items: List<NotificationItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ENTRY = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NotificationItem.Header -> TYPE_HEADER
            is NotificationItem.Entry -> TYPE_ENTRY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemNotificationHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            else -> {
                val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                EntryViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is NotificationItem.Header -> (holder as HeaderViewHolder).bind(item)
            is NotificationItem.Entry -> (holder as EntryViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size

    inner class HeaderViewHolder(val binding: ItemNotificationHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: NotificationItem.Header) {
            binding.tvHeader.text = header.title
        }
    }

    inner class EntryViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: NotificationItem.Entry) {
            val notification = entry.notification
            binding.tvTitle.text = notification.title
            binding.tvDescription.text = notification.description
            binding.tvTime.text = notification.time

            if (entry.isNew) {
                // Blue Card (Solid Blue) using color resource or hex
                binding.cardNotification.setCardBackgroundColor(android.graphics.Color.parseColor("#5C6BC0")) // Primary Blue Variant
                binding.cardNotification.strokeWidth = 0
                
                binding.tvTitle.setTextColor(android.graphics.Color.WHITE)
                binding.tvDescription.setTextColor(android.graphics.Color.WHITE)
                binding.tvTime.setTextColor(android.graphics.Color.WHITE)
                
                // Icon: Tint White, Background White (or lighter blue)
                binding.ivIcon.setColorFilter(android.graphics.Color.parseColor("#5C6BC0")) // Icon itself blue? No, usually white on blue bg.
                // Screenshot: White Circle, Blue Icon inside? Or White Icon on Blue Card.
                // Screenshot check: "Your booking is successful" -> Blue Card. Icon is white circle with Blue bell inside.
                binding.ivIcon.background.setTint(android.graphics.Color.WHITE)
                binding.ivIcon.setColorFilter(android.graphics.Color.parseColor("#5C6BC0"))
            } else {
                // White Card
                binding.cardNotification.setCardBackgroundColor(android.graphics.Color.WHITE)
                binding.cardNotification.strokeColor = android.graphics.Color.parseColor("#E0E0E0")
                binding.cardNotification.strokeWidth = 2 // 1dp usually approx 2-3px or set in xml. XML has 1dp.
                
                binding.tvTitle.setTextColor(android.graphics.Color.BLACK)
                binding.tvDescription.setTextColor(android.graphics.Color.parseColor("#757575"))
                binding.tvTime.setTextColor(android.graphics.Color.parseColor("#757575"))
                
                // Icon: Gray bell, Grayish circle
                binding.ivIcon.background.setTint(android.graphics.Color.parseColor("#F5F5F5"))
                binding.ivIcon.setColorFilter(android.graphics.Color.parseColor("#757575"))
            }
        }
    }
}
