package com.example.auvan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.auvan.databinding.ItemSeatBinding
import com.example.auvan.databinding.ItemSeatDriverBinding
import com.example.auvan.databinding.ItemSeatGapBinding

enum class SeatStatus { AVAILABLE, BOOKED, SELECTED }

sealed class SeatGridItem {
    data class SeatItem(val id: String, val name: String, var status: SeatStatus) : SeatGridItem()
    object Driver : SeatGridItem()
    object Gap : SeatGridItem()
}

class SeatAdapter(
    private var items: List<SeatGridItem>,
    private val onSeatClick: (SeatGridItem.SeatItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_SEAT = 0
        const val TYPE_DRIVER = 1
        const val TYPE_GAP = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SeatGridItem.SeatItem -> TYPE_SEAT
            is SeatGridItem.Driver -> TYPE_DRIVER
            is SeatGridItem.Gap -> TYPE_GAP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SEAT -> {
                val binding = ItemSeatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SeatViewHolder(binding)
            }
            TYPE_DRIVER -> {
                val binding = ItemSeatDriverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DriverViewHolder(binding)
            }
            else -> {
                val binding = ItemSeatGapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                GapViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is SeatGridItem.SeatItem -> (holder as SeatViewHolder).bind(item)
            is SeatGridItem.Driver -> (holder as DriverViewHolder).bind()
            is SeatGridItem.Gap -> (holder as GapViewHolder).bind()
        }
    }

    override fun getItemCount() = items.size
    
    fun updateData(newItems: List<SeatGridItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class SeatViewHolder(val binding: ItemSeatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(seat: SeatGridItem.SeatItem) {
            binding.tvSeatName.text = seat.name
            
            // Default Available Style (Gray bg, Blue Text)
            // Booked Style (Blue bg, White Text)
            
            when (seat.status) {
                SeatStatus.AVAILABLE -> {
                    binding.innerContainer.setBackgroundResource(R.drawable.bg_seat_available)
                    binding.tvSeatName.setTextColor(android.graphics.Color.parseColor("#3F51B5")) // Primary
                    binding.ivSeatIcon.setColorFilter(android.graphics.Color.parseColor("#3F51B5"))
                }
                SeatStatus.BOOKED -> {
                    binding.innerContainer.setBackgroundResource(R.drawable.bg_seat_booked)
                    binding.tvSeatName.setTextColor(android.graphics.Color.parseColor("#3F51B5")) // Primary Blue
                    binding.ivSeatIcon.setColorFilter(android.graphics.Color.parseColor("#3F51B5"))
                }
                SeatStatus.SELECTED -> {
                     binding.innerContainer.setBackgroundResource(R.drawable.bg_seat_available)
                     // Selected styling (maybe dark blue border or different color)
                     binding.innerContainer.background.setTint(android.graphics.Color.parseColor("#FFC107")) // Yellow? Or keep Blue.
                     // For pixel perfect based on screenshot, 'Booked' is Blue. Available is Gray. 
                     // Selected usually matches Booked or High contrast. I'll make selected Orange for visibility.
                     binding.tvSeatName.setTextColor(android.graphics.Color.WHITE)
                     binding.ivSeatIcon.setColorFilter(android.graphics.Color.WHITE)
                }
            }

            binding.root.setOnClickListener {
                if (seat.status != SeatStatus.BOOKED) {
                    onSeatClick(seat)
                }
            }
        }
    }

    inner class DriverViewHolder(val binding: ItemSeatDriverBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            // Static driver icon
        }
    }

    inner class GapViewHolder(val binding: ItemSeatGapBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            // Empty space
        }
    }
}
