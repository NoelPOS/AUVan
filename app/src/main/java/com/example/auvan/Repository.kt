package com.example.auvan

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

object Repository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    // In-memory cache for UI (optional, but good for immediate display)
    // Note: Ideally, viewmodels should observe a Flow or LiveData
    var bookings = mutableListOf<Booking>()
    var notifications = mutableListOf<Notification>()

    // Fetch bookings for current user
    fun listenToBookings(onUpdate: (List<Booking>) -> Unit): ListenerRegistration? {
        val user = auth.currentUser ?: return null

        return db.collection("bookings")
            .whereEqualTo("userId", user.uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val bookingList = snapshot.toObjects(Booking::class.java)
                    bookings.clear()
                    bookings.addAll(bookingList)
                    onUpdate(bookingList)
                }
            }
    }

    fun addBooking(booking: Booking, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        val bookingMap = hashMapOf(
            "id" to booking.id,
            "userId" to user.uid,
            "route" to booking.route,
            "date" to booking.date,
            "time" to booking.time,
            "seat" to booking.seat,
            "passengerName" to booking.passengerName,
            "passengerPhone" to booking.passengerPhone,
            "place" to booking.place, // Save the specific pickup location
            "status" to "Active"
        )

        db.collection("bookings").document(booking.id)
            .set(bookingMap)
            .addOnSuccessListener {
                val notification = Notification(
                    id = System.currentTimeMillis().toString(),
                    userId = user.uid,
                    title = "Your booking is successful!",
                    description = "You will be notified when you driver get ready.",
                    time = "Now",
                    timestamp = System.currentTimeMillis()
                )
                db.collection("notifications").document(notification.id).set(notification)
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    suspend fun getBooking(id: String): Booking? {
        return try {
            val snapshot = db.collection("bookings").document(id).get().await()
            snapshot.toObject(Booking::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun updateBooking(updatedBooking: Booking, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
         db.collection("bookings").document(updatedBooking.id)
            .set(updatedBooking)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun cancelBooking(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = auth.currentUser ?: return
        db.collection("bookings").document(id)
            .update("status", "Cancelled")
            .addOnSuccessListener {
                val notification = Notification(
                    id = System.currentTimeMillis().toString(),
                    userId = user.uid,
                    title = "You have canceled your ride!", 
                    description = "Tap here to track status.", 
                    time = "Now",
                    timestamp = System.currentTimeMillis()
                )
                db.collection("notifications").document(notification.id).set(notification)
                onSuccess()
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun saveUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(user.id)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun updateUser(userId: String, data: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId)
            .update(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getUser(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                onSuccess(snapshot.toObject(User::class.java))
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun seedDatabase(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        // 1. Delete all existing bookings for this user
        db.collection("bookings")
            .whereEqualTo("userId", user.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                for (document in snapshot.documents) {
                    batch.delete(document.reference)
                }

                // 2. Add new sample bookings
                val sampleBookings = listOf(
                    Booking(
                        id = java.util.UUID.randomUUID().toString(),
                        userId = user.uid,
                        route = "Assumption University to Siam",
                        date = "Mon, Oct 25 2026",
                        time = "08:00 AM",
                        seat = "1A",
                        passengerName = "Test User",
                        passengerPhone = "0812345678",
                        place = "Assumption University",
                        status = "Active"
                    ),
                    Booking(
                        id = java.util.UUID.randomUUID().toString(),
                        userId = user.uid,
                        route = "Siam to Assumption University",
                        date = "Tue, Oct 26 2026",
                        time = "05:00 PM",
                        seat = "2B",
                        passengerName = "Test User",
                        passengerPhone = "0812345678",
                        place = "Siam Center",
                        status = "Active"
                    ),
                    Booking(
                        id = java.util.UUID.randomUUID().toString(),
                        userId = user.uid,
                        route = "Assumption University to Siam",
                        date = "Wed, Oct 20 2026", // Past date
                        time = "09:00 AM",
                        seat = "3C",
                        passengerName = "Test User",
                        passengerPhone = "0812345678",
                        place = "Assumption University",
                        status = "Completed"
                    ),
                    Booking(
                        id = java.util.UUID.randomUUID().toString(),
                        userId = user.uid,
                        route = "Siam to Assumption University",
                        date = "Thu, Oct 21 2026",
                        time = "06:00 PM",
                        seat = "4D",
                        passengerName = "Test User",
                        passengerPhone = "0812345678",
                        place = "Siam Paragon",
                        status = "Cancelled"
                    )
                )

                sampleBookings.forEach { booking ->
                    val docRef = db.collection("bookings").document(booking.id)
                    batch.set(docRef, booking)
                }
                
                // 3. Create Default User Profile
                val userProfile = User(
                    id = user.uid,
                    name = "Test Student",
                    email = user.email ?: "test@au.edu",
                    phone = "0812345678",
                    studentId = "6612345"
                )
                val userRef = db.collection("users").document(user.uid)
                batch.set(userRef, userProfile)

                batch.commit()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFailure(e) }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun fetchBookedSeats(date: String, time: String, route: String, onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("bookings")
            .whereEqualTo("date", date)
            .whereEqualTo("time", time)
            .whereEqualTo("route", route)
            .whereEqualTo("status", "Active") // Only check active bookings
            .get()
            .addOnSuccessListener { snapshot ->
                val bookedSeats = snapshot.documents.mapNotNull { it.getString("seat") }
                onSuccess(bookedSeats)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getAvailableSeats(date: String, time: String, route: String, onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("bookings")
            .whereEqualTo("date", date)
            .whereEqualTo("time", time)
            .whereEqualTo("route", route)
            .whereEqualTo("status", "Active")
            .get()
            .addOnSuccessListener { snapshot ->
                val bookedCount = snapshot.size()
                val totalSeats = 15 // Assuming 15 seats per van
                val available = totalSeats - bookedCount
                 onSuccess(if (available < 0) 0 else available)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun listenToNotifications(onUpdate: (List<Notification>) -> Unit): ListenerRegistration? {
        val user = auth.currentUser ?: return null

        return db.collection("notifications")
            .whereEqualTo("userId", user.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val notificationList = snapshot.toObjects(Notification::class.java)
                    notifications.clear()
                    notifications.addAll(notificationList)
                    onUpdate(notificationList)
                }
            }
    }
}
