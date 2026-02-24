package com.example.auvan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.auvan.databinding.ItemTimeslotBinding

data class Timeslot(val time: String, val seats: Int, val price: String)

class TimeslotAdapter(
    initialTimeslots: List<Timeslot>,
    private val onTimeslotClick: (Timeslot) -> Unit
) : RecyclerView.Adapter<TimeslotAdapter.TimeslotViewHolder>() {

    private val timeslots = initialTimeslots.toMutableList()

    inner class TimeslotViewHolder(val binding: ItemTimeslotBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeslotViewHolder {
        val binding = ItemTimeslotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeslotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeslotViewHolder, position: Int) {
        val timeslot = timeslots[position]
        holder.binding.tvTime.text = timeslot.time
        holder.binding.tvSeats.text = "${timeslot.seats} seats"
        holder.binding.tvPrice.text = timeslot.price

        if (timeslot.seats == 0) {
            holder.binding.tvSeats.text = "0 seats"
            holder.binding.tvSeats.setTextColor(android.graphics.Color.RED)
            holder.binding.ivSeat.drawable.setTint(android.graphics.Color.RED)
            holder.binding.root.alpha = 0.5f // Dim if full
        } else if (timeslot.seats < 5) {
            holder.binding.tvSeats.setTextColor(android.graphics.Color.parseColor("#FF9800")) // Orange
            holder.binding.ivSeat.drawable.setTint(android.graphics.Color.parseColor("#FF9800"))
            holder.binding.root.alpha = 1.0f
        } else {
             holder.binding.tvSeats.setTextColor(android.graphics.Color.parseColor("#4CAF50")) // Green
             holder.binding.ivSeat.drawable.setTint(android.graphics.Color.parseColor("#4CAF50"))
             holder.binding.root.alpha = 1.0f
        }

        holder.itemView.setOnClickListener {
            onTimeslotClick(timeslot)
        }
    }

    override fun getItemCount() = timeslots.size

    fun updateData(newTimeslots: List<Timeslot>) {
        (timeslots as MutableList).clear()
        timeslots.addAll(newTimeslots)
        notifyDataSetChanged()
    }
}
