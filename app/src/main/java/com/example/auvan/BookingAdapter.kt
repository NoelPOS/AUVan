package com.example.auvan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.auvan.databinding.ItemBookingBinding
import com.example.auvan.databinding.ItemBookingHeaderBinding

sealed class BookingListItem {
    data class Header(val date: String) : BookingListItem()
    data class Item(val booking: Booking) : BookingListItem()
}

class BookingAdapter(
    private var items: List<BookingListItem>,
    private val onBookingClick: (Booking) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is BookingListItem.Header -> TYPE_HEADER
            is BookingListItem.Item -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemBookingHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }
            else -> {
                val binding = ItemBookingBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                BookingViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is BookingListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is BookingListItem.Item -> (holder as BookingViewHolder).bind(item.booking) // Corrected from .Item to .booking
        }
    }

    override fun getItemCount() = items.size
    
    fun updateData(newItems: List<BookingListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class HeaderViewHolder(val binding: ItemBookingHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: BookingListItem.Header) {
            binding.tvDateHeader.text = header.date
        }
    }

    inner class BookingViewHolder(val binding: ItemBookingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(booking: Booking) {
            binding.tvRoute.text = booking.route
            binding.tvDate.text = booking.date
            binding.tvTime.text = booking.time
            binding.tvSeat.text = booking.seat
            // Assuming status handling logic if needed for different styling
            
            binding.btnDetails.setOnClickListener {
                onBookingClick(booking)
            }
            binding.root.setOnClickListener {
                onBookingClick(booking)
            }
        }
    }
}
