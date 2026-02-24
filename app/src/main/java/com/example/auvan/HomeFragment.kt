package com.example.auvan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.auvan.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Date to Today
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("EEE, MMM dd yyyy", java.util.Locale.US)
        val today = dateFormat.format(calendar.time)
        binding.tvDate.text = today
        
        // setupSpinners()
        setupRecyclerView()
        setupDatePicker()
        setupLocationSelectors()
    }

    private fun setupLocationSelectors() {
        val locations = arrayOf("Assumption University", "Siam", "Megabangna")

        binding.cardFrom.setOnClickListener {
            showLocationDialog(true, locations)
        }

        binding.cardTo.setOnClickListener {
            showLocationDialog(false, locations)
        }
    }

    private fun showLocationDialog(isFrom: Boolean, locations: Array<String>) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (isFrom) "Select Origin" else "Select Destination")
            .setItems(locations) { _, which ->
                val selectedLocation = locations[which]
                
                if (isFrom) {
                    if (selectedLocation == binding.tvToValue.text.toString()) {
                        android.widget.Toast.makeText(requireContext(), "Origin and Destination cannot be the same", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        binding.tvFromValue.text = selectedLocation
                        updateRouteAndRefresh()
                    }
                } else {
                    if (selectedLocation == binding.tvFromValue.text.toString()) {
                        android.widget.Toast.makeText(requireContext(), "Origin and Destination cannot be the same", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        binding.tvToValue.text = selectedLocation
                        updateRouteAndRefresh()
                    }
                }
            }
            .show()
    }

    private fun updateRouteAndRefresh() {
        val route = "${binding.tvFromValue.text} - ${binding.tvToValue.text}"
        binding.tvRouteSubtitle.text = route
        setupRecyclerView()
    }

    private fun setupDatePicker() {
        binding.cardDate.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH)
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

            val datePickerDialog = android.app.DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = java.util.Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                    val dateFormat = java.text.SimpleDateFormat("EEE, MMM dd yyyy", java.util.Locale.US)
                    val selectedDate = dateFormat.format(selectedCalendar.time)
                    
                    binding.tvDate.text = selectedDate
                    binding.tvDate.setTextColor(android.graphics.Color.BLACK)
                    
                    // Refresh RecyclerView for new date
                    setupRecyclerView()
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }

    // Spinners replaced by static MaterialCards for UI match
    // private fun setupSpinners() { ... }

    private fun setupRecyclerView() {
        val defaultTimeslots = mutableListOf<Timeslot>()
        for (hour in 9..22) { // 9 AM to 10 PM
             val calendar = java.util.Calendar.getInstance()
             calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
             calendar.set(java.util.Calendar.MINUTE, 0)
             val timeFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.US)
             val timeString = timeFormat.format(calendar.time)
             defaultTimeslots.add(Timeslot(timeString, 15, "50 THB"))
        }
        
        // Initialize with default
        val adapter = TimeslotAdapter(defaultTimeslots) { timeslot ->
             // Navigate to Seat Selection with selected time
            val bundle = Bundle().apply { 
                putString("time", timeslot.time)
                putString("date", binding.tvDate.text.toString())
                putString("route", binding.tvRouteSubtitle.text.toString()) // Dynamically get route
            }
            findNavController().navigate(R.id.action_homeFragment_to_seatSelectionFragment, bundle)
        }
        binding.rvTimeslots.adapter = adapter
        
        // Update with real data
        val date = binding.tvDate.text.toString()
        val route = binding.tvRouteSubtitle.text.toString() // Dynamically get route
        
        val updatedTimeslots = defaultTimeslots.toMutableList()
        var completedFetches = 0
        
        updatedTimeslots.forEachIndexed { index, slot ->
             // Use slot.time directly as it is now standardized
             Repository.getAvailableSeats(date, slot.time, route,
                 onSuccess = { seats ->
                      updatedTimeslots[index] = slot.copy(seats = seats)
                      completedFetches++
                      if (completedFetches == updatedTimeslots.size) {
                           adapter.updateData(updatedTimeslots)
                      }
                 },
                 onFailure = {
                      completedFetches++ // Count it anyway to avoid hanging
                      if (completedFetches == updatedTimeslots.size) {
                           adapter.updateData(updatedTimeslots)
                      }
                 }
             )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
