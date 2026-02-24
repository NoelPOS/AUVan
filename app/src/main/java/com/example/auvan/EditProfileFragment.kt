package com.example.auvan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.auvan.databinding.FragmentEditProfileBinding // Make sure this is generated, or I will use findViewById if binding not generated yet, but standard is binding.
import com.google.firebase.auth.FirebaseAuth

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            Repository.getUser(currentUser.uid,
                onSuccess = { user ->
                    user?.let {
                        binding.etName.setText(it.name)
                        binding.etPhone.setText(it.phone)
                        binding.etStudentId.setText(it.studentId)
                        binding.etLocation.setText(it.location)
                    }
                },
                onFailure = {
                    Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val phone = binding.etPhone.text.toString()
            val studentId = binding.etStudentId.text.toString()
            val location = binding.etLocation.text.toString()

            if (name.isBlank()) {
                binding.tilName.error = "Name is required"
                return@setOnClickListener
            }
            
            val userId = currentUser?.uid ?: return@setOnClickListener

            val updates = mapOf(
                "name" to name,
                "phone" to phone,
                "studentId" to studentId,
                "location" to location
            )

            binding.btnSave.isEnabled = false
            
            Repository.updateUser(userId, updates,
                onSuccess = {
                    Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                },
                onFailure = { e ->
                    binding.btnSave.isEnabled = true
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
