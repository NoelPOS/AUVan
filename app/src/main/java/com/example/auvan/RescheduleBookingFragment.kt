package com.example.auvan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.auvan.databinding.FragmentRescheduleBookingBinding

class RescheduleBookingFragment : Fragment() {

    private var _binding: FragmentRescheduleBookingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRescheduleBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val defaultRoute = "Assumption University to Siam" // Consistent default

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private var currentBookingId: String? = null

    private fun setupListeners() {
        currentBookingId = arguments?.getString("bookingId")
        
        currentBookingId?.let { id ->
            // Use lifecycleScope to call suspend function
            viewLifecycleOwner.lifecycleScope.launch {
                val booking = Repository.getBooking(id)
                booking?.let {
                    // Pre-fill existing data
                    binding.etName.setText(it.passengerName)
                    binding.etPhone.setText(it.passengerPhone)
                    binding.etPlace.setText(it.place)
                    
                    val route = if (it.route.isBlank()) defaultRoute else it.route
                    binding.tvRoute.text = route
                    binding.tvDate.text = it.date
                    binding.tvTime.text = it.time
                    binding.tvSeat.text = it.seat
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            if (binding.etName.text.isNullOrBlank()) {
                binding.etName.error = "Name is required"
                return@setOnClickListener
            }
            
            // In a real app, pick new date/time here. For now just updating current booking.
            currentBookingId?.let { id ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val current = Repository.getBooking(id)
                    if (current != null) {
                         // Update status or details if needed. 
                        Repository.updateBooking(current.copy(
                            passengerName = binding.etName.text.toString(),
                            passengerPhone = binding.etPhone.text.toString(),
                            place = binding.etPlace.text.toString(),
                            status = "Active" // Keep active or update as needed
                        ), 
                            onSuccess = {
                                Toast.makeText(requireContext(), "Booking Updated Successfully", Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            },
                            onFailure = { e ->
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        ) 
                    }
                }
            }
        }
        
        binding.btnCancelBooking.setOnClickListener {
             currentBookingId?.let { id ->
                 Repository.cancelBooking(id,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Booking Cancelled", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    },
                    onFailure = { e ->
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                 )
             }
        }
        
        binding.ivEdit.setOnClickListener {
             Toast.makeText(requireContext(), "Edit Route/Time clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
