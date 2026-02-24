# Firebase Integration Guide for AUVan

This guide outlines the steps to migrate AUVan from in-memory mock data to a robust Firebase backend.

## 1. Firebase Setup

### Step 1: Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/).
2. Create a new project "AUVan".
3. Enable **Google Analytics** (optional).

### Step 2: Register Android App
1. In Firebase Console, click the Android icon.
2. Enter Package Name: `com.example.auvan`.
3. Download `google-services.json` and place it in the `app/` directory.

### Step 3: Add Dependencies
Modify `build.gradle.kts` (Module: app):
```kotlin
plugins {
    id("com.google.gms.google-services")
}

dependencies {
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Declare the dependencies for the Firebase products you need
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
}
```
Modify `build.gradle.kts` (Project level) to include the google-services classpath.

## 2. Authentication (Login/Logout)

### Feature: Sign Up & Login
- **Current State**: `OnboardingFragment` leads directly to Home.
- **New Flow**: `Onboarding` -> `LoginFragment` / `SignUpFragment` -> `Home`.

### Implementation Details
1. **Create UI**: Create `fragment_login.xml` and `fragment_signup.xml`.
2. **FirebaseAuth**:
   - Use `FirebaseAuth.getInstance()`.
   - `createUserWithEmailAndPassword(email, password)` for Sign Up.
   - `signInWithEmailAndPassword(email, password)` for Login.
   - `signOut()` for Logout (in `ProfileFragment`).

### User Data in Firestore
When a user signs up, save their additional details (Name, Student ID) to Firestore:
```
Collection: users
Document ID: {uid}
Fields:
  - email: String
  - name: String
  - studentId: String
  - role: "student" | "driver"
```

## 3. Database Migration (Firestore)

Refactor `Repository.kt` to fetch/store data in Firestore instead of local lists.

### Schema Design

#### Collection: `bookings`
Stores all ride bookings.
```
Document ID: {auto-mod-id}
Fields:
  - userId: {uid} (Reference to user)
  - route: String
  - date: Timestamp
  - time: String
  - seatNumber: String
  - status: "active" | "completed" | "cancelled"
  - createdAt: Timestamp
```

#### Collection: `schedules` (Optional/Advanced)
To manage available seats dynamically.
```
Document ID: {route}_{date}_{time}
Fields:
  - route: String
  - capacity: Number (e.g., 10)
  - bookedSeats: Array<String> (e.g., ["1A", "2B"])
```

### Refactoring `Repository`
Change functions to be **suspend** functions or use **Callbacks/Flow** because Firebase is asynchronous.

**Example:**
```kotlin
// Old
fun getBooking(id: String): Booking? { return bookings.find... }

// New (Coroutines)
suspend fun getBooking(id: String): Booking? {
    val snapshot = firestore.collection("bookings").document(id).get().await()
    return snapshot.toObject(Booking::class.java)
}
```

## 4. Notifications (FCM)
1. **Setup**: Add `firebase-messaging` dependency.
2. **Implementation**: Create `MyFirebaseMessagingService` to handle incoming messages.
3. **Trigger**: Use Firebase Cloud Functions to trigger a notification when a booking status changes (e.g., "Driver is here").

## 5. Security Rules
Set up Firestore Security Rules to ensure users can only read/write their own data.
```
match /bookings/{bookingId} {
  allow read, write: if request.auth != null && request.auth.uid == resource.data.userId;
}
```
