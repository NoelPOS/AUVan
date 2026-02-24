# AUVan System Documentation

## 1. Project Overview
**AUVan** is an Android application designed for booking van shuttles between Assumption University (AU) campuses and other locations (e.g., Siam, Mega Bangna). It features a user-friendly interface for browsing schedules, selecting seats, and managing bookings.

## 2. Technical Architecture
The app follows a Single Activity Architecture using `MainActivity` as the container and `Navigation Component` for screen transitions. It uses a **MVVM-like** pattern where:
- **View**: Fragments (`HomeFragment`, `BookingFragment`, etc.) handle UI and user interaction.
- **Model**: Data classes (`Booking`, `Notification`) define the data structure.
- **Data Source**: `Repository` object acts as a singleton source of truth (currently using in-memory mock data).

### Key Technologies
- **Language**: Kotlin
- **UI Toolkit**: XML Layouts, ViewBinding, Material Design Components
- **Navigation**: Android Jetpack Navigation
- **Build System**: Gradle (Kotlin DSL)

## 3. Screen/Feature Breakdown

### 3.1 Onboarding
- **File**: `OnboardingFragment.kt`
- **Function**: The entry point for new/unauthenticated users.
- **Features**: Introduces the app and leads to the Home screen (or Login in future).

### 3.2 Home
- **File**: `HomeFragment.kt`
- **Function**: The main dashboard.
- **Features**:
    - "Book Now" shortcut.
    - Status overview (e.g., "Next shuttle in 15 mins").
    - Quick access to recent activity.

### 3.3 Booking Flow
The core feature of the app, split into multiple steps:
1.  **Route Selection**: (Part of Home or Booking initial step)
2.  **Seat Selection**:
    - **File**: `SeatSelectionFragment.kt`
    - **Logic**: Visual grid of seats. Users tap to select available seats.
3.  **Timeslot Selection**:
    - **Adapter**: `TimeslotAdapter.kt`
    - **Logic**: List of available departure times.
4.  **Confirmation**:
    - **File**: `BookingConfirmationFragment.kt`
    - **Logic**: Review details and confirm.
    - **Data**: Saves booking to `Repository`.

### 3.4 My Bookings
- **File**: `MyBookingsFragment.kt`
- **Adapter**: `BookingAdapter.kt`
- **Function**: Displays a list of user's active and past bookings.
- **Features**:
    - View details of a specific booking.
    - **Reschedule**: `RescheduleBookingFragment.kt` allows changing time/seat.
    - **Cancel**: Removes booking from active list.

### 3.5 Notifications
- **File**: `NotificationsFragment.kt`
- **Adapter**: `NotificationAdapter.kt`
- **Function**: a list of system alerts.
- **Data**: Pulled from `Repository.notifications`.

### 3.6 Profile
- **File**: `ProfileFragment.kt`
- **Function**: User settings and profile management.
- **Features**:
    - Start Edit Profile (UI only).
    - **Dark Mode Switch**: Toggles app-wide theme using `AppCompatDelegate` and saves preference to `SharedPreferences`.
    - Log Out.

## 4. Data Models

### Booking
```kotlin
data class Booking(
    val id: String,
    val route: String,
    val date: String,
    val time: String,
    val seat: String,
    val status: String // e.g., "Completed", "Cancelled", "Active"
)
```

### Notification
```kotlin
data class Notification(
    val id: String,
    val title: String,
    val description: String,
    val time: String
)
```

## 5. Data Management (Repository)
**File**: `Repository.kt`
Currently, the app uses a Singleton object `Repository` to store data in-memory.
- `bookings`: MutableList of `Booking` objects.
- `notifications`: MutableList of `Notification` objects.
- **Methods**: `addBooking`, `getBooking`, `updateBooking`, `cancelBooking`.

> **Note**: This data is reset when the app is killed. For production, this must be replaced with a persistent backend (e.g., Firebase).
