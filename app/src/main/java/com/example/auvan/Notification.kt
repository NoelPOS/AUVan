package com.example.auvan

data class Notification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val time: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
