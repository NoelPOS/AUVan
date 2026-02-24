package com.example.auvan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.auvan.databinding.FragmentBookingFormBinding

class BookingFormFragment : Fragment() {

    private var _binding: FragmentBookingFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch and auto-fill user profile data
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            Repository.getUser(currentUser.uid,
                onSuccess = { user ->
                    _binding?.let { // Guard against fragment being detached
                        if (user != null) {
                            if (it.etName.text.isBlank()) it.etName.setText(user.name)
                            if (it.etPhone.text.isBlank()) it.etPhone.setText(user.phone)
                            if (it.etPlace.text.isBlank()) it.etPlace.setText(user.location)
                        }
                    }
                },
                onFailure = {
                    // Optional: handle failure or just leave empty for manual entry
                }
            )
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val phone = binding.etPhone.text.toString()
            val place = binding.etPlace.text.toString()
            
            if (name.isEmpty()) {
                binding.etName.error = "Name is required"
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                binding.etPhone.error = "Phone is required"
                return@setOnClickListener
            }

            // In a real app, save booking and navigate
            val seatNumber = arguments?.getString("seatNumber") ?: "1A"
            val date = arguments?.getString("date") ?: "Mon, Oct 25 2026" 
            val time = arguments?.getString("time") ?: "08:00 AM" 
            val route = arguments?.getString("route") ?: "Assumption University to Siam" 

            // Update Details Card
            // Note: We need ids for these TextViews in XML first. 
            // Assuming ids: tvDetailDate, tvDetailTime, tvDetailSeat, tvDetailRoute
            // I will add these IDs in the next XML edit step.
            binding.tvDetailDate.text = date
            binding.tvDetailTime.text = time
            binding.tvDetailSeat.text = seatNumber
            binding.tvDetailRoute.text = route
            
            // Generate a random ID or let Firestore handle it. Repository.addBooking expects an ID in the object.
            val bookingId = java.util.UUID.randomUUID().toString()
            
            val booking = Booking(
                id = bookingId,
                route = route, // Use the fixed route from arguments
                date = date,
                time = time,
                seat = seatNumber,
                passengerName = name,
                passengerPhone = phone,
                place = place, // Use the user input for specific place
                status = "Scheduled"
            )
            
            // Disable button to prevent double clicks
            binding.btnSave.isEnabled = false
            
            Repository.addBooking(booking,
                onSuccess = {
                     if (isAdded) {
                         Toast.makeText(requireContext(), "Booking Saved!", Toast.LENGTH_SHORT).show()
                         findNavController().navigate(R.id.action_bookingFormFragment_to_bookingConfirmationFragment)
                     }
                },
                onFailure = { e ->
                    if (isAdded) {
                        binding.btnSave.isEnabled = true
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }



    private fun validateInput(): Boolean {
        var isValid = true
        if (binding.etName.text.isNullOrBlank()) {
            binding.etName.error = "Name is required"
            isValid = false
        }
        if (binding.etPhone.text.isNullOrBlank()) {
            binding.etPhone.error = "Phone is required"
            isValid = false
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
