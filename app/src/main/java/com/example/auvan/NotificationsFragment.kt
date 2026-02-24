package com.example.auvan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.auvan.databinding.FragmentNotificationsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

class NotificationsFragment : Fragment() {
 
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        listenerRegistration = Repository.listenToNotifications { notifications ->
            _binding?.let {
                setupRecyclerView(notifications)
            }
        }
    }

    private fun setupRecyclerView(notifications: List<Notification>) {
        val items = mutableListOf<NotificationItem>()
        
        // Group notifications by Date string
        val groupedNotifications = mutableMapOf<String, MutableList<Notification>>()
        val dateFormat = SimpleDateFormat("M/d/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        val calendar = Calendar.getInstance()
        val todayStr = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = dateFormat.format(calendar.time)

        for (notification in notifications) {
            val date = Date(notification.timestamp)
            val dateString = dateFormat.format(date)
            // Fix the time property if it's currently hardcoded "Now" by converting timestamp to a real time string
            val displayTime = if (notification.time == "Now") timeFormat.format(date) else notification.time

            // Create a copy of the notification with the properly formatted time for display
            val displayNotification = notification.copy(time = displayTime)

            if (!groupedNotifications.containsKey(dateString)) {
                groupedNotifications[dateString] = mutableListOf()
            }
            groupedNotifications[dateString]?.add(displayNotification)
        }

        // Build the RecyclerView Items list
        for ((dateStr, notificationList) in groupedNotifications) {
            val headerText = when (dateStr) {
                todayStr -> "TODAY"
                yesterdayStr -> "YESTERDAY\n$dateStr"
                else -> dateStr
            }
            
            items.add(NotificationItem.Header(headerText))
            
            // Assuming the first item in the whole list is the newest, or we can just say `isNew = true` for TODAY items.
            // Simplified logic: mark as new if it's from today.
            for (notification in notificationList) {
                items.add(NotificationItem.Entry(
                    notification,
                    isNew = (dateStr == todayStr)
                ))
            }
        }

        val adapter = NotificationAdapter(items)
        binding.rvNotifications.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
        _binding = null
    }
}
