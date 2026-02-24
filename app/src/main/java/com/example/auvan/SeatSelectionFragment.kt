package com.example.auvan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.auvan.databinding.FragmentSeatSelectionBinding

class SeatSelectionFragment : Fragment() {

    private var _binding: FragmentSeatSelectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SeatAdapter
    private val seatItems = mutableListOf<SeatGridItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeatSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        // Retrieve arguments first
        val date = arguments?.getString("date") ?: "Mon, Oct 25 2026"
        val time = arguments?.getString("time") ?: "08:00 AM"
        val route = arguments?.getString("route") ?: "Assumption University to Siam"

        seatItems.clear()
        
        // Define all seats first with default AVAILABLE status
        val allSeats = mutableListOf<SeatGridItem>()
        // Row 1
        allSeats.add(SeatGridItem.SeatItem("1", "1A", SeatStatus.AVAILABLE))
        allSeats.add(SeatGridItem.SeatItem("2", "1B", SeatStatus.AVAILABLE))
        allSeats.add(SeatGridItem.Gap)
        allSeats.add(SeatGridItem.Driver)

        // Row 2
        allSeats.add(SeatGridItem.Gap)
        allSeats.add(SeatGridItem.SeatItem("3", "2A", SeatStatus.AVAILABLE))
        allSeats.add(SeatGridItem.SeatItem("4", "2B", SeatStatus.AVAILABLE))
        allSeats.add(SeatGridItem.SeatItem("5", "2C", SeatStatus.AVAILABLE))

        // Row 3
        allSeats.add(SeatGridItem.SeatItem("6", "3A", SeatStatus.AVAILABLE))
        allSeats.add(SeatGridItem.Gap)
        allSeats.add(SeatGridItem.SeatItem("7", "3B", SeatStatus.AVAILABLE))
        allSeats.add(SeatGridItem.SeatItem("8", "3C", SeatStatus.AVAILABLE))

        // Row 4
        allSeats.add(SeatGridItem.SeatItem("9", "4A", SeatStatus.AVAILABLE))
        allSeats.add(SeatGridItem.Gap) 
        allSeats.add(SeatGridItem.SeatItem("10","4B", SeatStatus.AVAILABLE))
        allSeats.add(SeatGridItem.SeatItem("11","4C", SeatStatus.AVAILABLE))

        // Row 5
        allSeats.add(SeatGridItem.SeatItem("12","5A", SeatStatus.AVAILABLE))
        allSeats.add(SeatGridItem.SeatItem("13","5B", SeatStatus.AVAILABLE))
        allSeats.add(SeatGridItem.SeatItem("14","5C", SeatStatus.AVAILABLE))
        allSeats.add(SeatGridItem.SeatItem("15","5D", SeatStatus.AVAILABLE))
        
        seatItems.addAll(allSeats)

        adapter = SeatAdapter(seatItems) { selectedSeat ->
             // Handle Selection
            val currentList = seatItems.toMutableList()
            // Reset previous selection
            currentList.forEach { 
                if (it is SeatGridItem.SeatItem && it.status == SeatStatus.SELECTED) {
                    it.status = SeatStatus.AVAILABLE
                }
            }
            // Set new selection
            selectedSeat.status = SeatStatus.SELECTED
            adapter.updateData(currentList)
            
            // Navigate or Confirm
            // For now, Toast
            Toast.makeText(requireContext(), "Selected ${selectedSeat.name}", Toast.LENGTH_SHORT).show()
            // Ideally explicit button.
            // Navigate with seat info
            val bundle = Bundle().apply { 
                 putString("seatNumber", selectedSeat.name)
                 putString("date", date)
                 putString("time", time)
                 putString("route", route)
            }
            findNavController().navigate(R.id.action_seatSelectionFragment_to_bookingFormFragment, bundle)
        }
        
        binding.rvSeats.adapter = adapter
        
        // Fetch booked seats
        Repository.fetchBookedSeats(date, time, route,
            onSuccess = { bookedSeatNames ->
                // Update statuses
                val updatedList = seatItems.map { item ->
                    if (item is SeatGridItem.SeatItem && bookedSeatNames.contains(item.name)) {
                        item.copy(status = SeatStatus.BOOKED)
                    } else {
                        item
                    }
                }
                seatItems.clear()
                seatItems.addAll(updatedList)
                adapter.updateData(seatItems)
            },
            onFailure = {
                Toast.makeText(context, "Failed to load seat availability", Toast.LENGTH_SHORT).show()
            }
        )
        
        // Update Header UI
        binding.tvDate.text = "$date, $time"
        binding.tvFrom.text = route.split(" to ")[0]
        binding.tvTo.text = if (route.contains(" to ")) route.split(" to ")[1] else "Destination"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
