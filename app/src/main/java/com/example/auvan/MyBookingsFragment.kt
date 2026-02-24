package com.example.auvan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ListenerRegistration
import androidx.navigation.fragment.findNavController
import com.example.auvan.databinding.FragmentMyBookingsBinding

class MyBookingsFragment : Fragment() {

    private var _binding: FragmentMyBookingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BookingAdapter
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupToggleGroup()
        
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        

    }

    private var allBookings: List<Booking> = emptyList()

    private fun setupRecyclerView() {
        adapter = BookingAdapter(emptyList()) { booking ->
            val bundle = Bundle().apply {
                putString("bookingId", booking.id)
            }
            findNavController().navigate(R.id.action_myBookingsFragment_to_rescheduleBookingFragment, bundle)
        }
        binding.rvBookings.adapter = adapter
        
        listenerRegistration = Repository.listenToBookings { bookings ->
            _binding?.let { // Check if binding still exists
                allBookings = bookings
                val isCompleted = binding.btnCompleted.currentTextColor == android.graphics.Color.WHITE
                updateList(isCompleted)
            }
        }
    }
    
    private fun updateList(isActive: Boolean) {
        val filtered = if (isActive) {
             // Show Active, Scheduled, and Completed in the main list
             allBookings.filter { it.status == "Active" || it.status == "Scheduled" || it.status == "Completed" }
        } else {
             allBookings.filter { it.status == "Cancelled" }
        }
        adapter.updateData(processBookings(filtered))
    }

    private fun setupToggleGroup() {
        binding.btnCompleted.setOnClickListener {
            updateToggleState(true)
            updateList(true)
        }
        
        binding.btnCancelled.setOnClickListener {
            updateToggleState(false)
            updateList(false)
        }
    }

    private fun updateToggleState(isCompleted: Boolean) {
        if (isCompleted) {
             binding.btnCompleted.setBackgroundResource(R.drawable.bg_segmented_selected)
             binding.btnCompleted.setTextColor(android.graphics.Color.WHITE)
             
             binding.btnCancelled.setBackgroundResource(0) // Transparent
             binding.btnCancelled.setTextColor(android.graphics.Color.parseColor("#3F51B5")) // Primary Blue
        } else {
             binding.btnCancelled.setBackgroundResource(R.drawable.bg_segmented_selected)
             binding.btnCancelled.setTextColor(android.graphics.Color.WHITE)
             
             binding.btnCompleted.setBackgroundResource(0)
             binding.btnCompleted.setTextColor(android.graphics.Color.parseColor("#3F51B5"))
        }
    }
    
    private fun processBookings(bookings: List<Booking>): List<BookingListItem> {
        val grouped = bookings.groupBy { it.date } // Assuming date is "Sun, June 15 2025" or similar unique date
        val items = mutableListOf<BookingListItem>()
        
        grouped.forEach { (date, bookingList) ->
            // Convert "Sun, June 15 2025" to "15/6/2025" for header if needed, or just use as is. 
            // For now using raw date string as header.
            items.add(BookingListItem.Header(date))
            bookingList.forEach { booking ->
                items.add(BookingListItem.Item(booking)) // Fixed to match sealed class format
            }
        }
        return items
    }



    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
        _binding = null
    }
}
