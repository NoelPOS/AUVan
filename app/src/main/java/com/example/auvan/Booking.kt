package com.example.auvan

data class Booking(
    val id: String = "",
    val route: String = "",
    val date: String = "",
    val time: String = "",
    val seat: String = "",
    val userId: String = "",
    val passengerName: String = "",
    val passengerPhone: String = "",
    val place: String = "", // Added for specific pickup location
    val status: String = "" // e.g., "Completed", "Cancelled", "Active"
)
