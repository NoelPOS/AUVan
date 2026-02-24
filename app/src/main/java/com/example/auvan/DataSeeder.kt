package com.example.auvan

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

object DataSeeder {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun seedInitialData(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        val bookings = listOf(
            Booking(
                id = UUID.randomUUID().toString(),
                route = "Assumption University to Siam",
                date = "Sun, June 15 2025",
                time = "19:00",
                seat = "1A",
                status = "Completed"
            ),
            Booking(
                id = UUID.randomUUID().toString(),
                route = "Siam to Assumption University",
                date = "Sat, June 14 2025",
                time = "08:00",
                seat = "2B",
                status = "Completed"
            ),
            Booking(
                id = UUID.randomUUID().toString(),
                route = "Assumption University to Mega Bangna",
                date = "Fri, June 13 2025",
                time = "17:00",
                seat = "3C",
                status = "Cancelled"
            )
        )

        var successCount = 0
        bookings.forEach { booking ->
            val bookingMap = hashMapOf(
                "id" to booking.id,
                "userId" to user.uid,
                "route" to booking.route,
                "date" to booking.date,
                "time" to booking.time,
                "seat" to booking.seat,
                "status" to booking.status
            )

            db.collection("bookings").document(booking.id)
                .set(bookingMap)
                .addOnSuccessListener {
                    successCount++
                    if (successCount == bookings.size) {
                        onSuccess()
                    }
                }
                .addOnFailureListener { e ->
                    // Log error but continue
                }
        }
    }
}
