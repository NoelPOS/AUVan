# AU Van: Assumption University Van Booking System 🚌

AU Van is a modern Android application designed to simplify the van booking process for students and staff at Assumption University. It provides a real-time, user-friendly experience for selecting routes, timeslots, and specific seats on internal campus shuttles and external van services.

---

## ✨ Key Features

- **📍 Dynamic Route Selection:** Choose between campus hubs and popular city destinations (e.g., Siam, Megabangna).
- **⏰ Hourly Timeslots:** View available vans for every hour from 9 AM to 10 PM.
- **💺 Visual Seat Selection:** An interactive van map with real-time availability. Choose your favorite seat!
- **⚡ Smart Auto-fill:** Saves time by automatically populating your Name, Phone, and Preferred Location from your user profile.
- **🕒 Real-time Notifications:** Get live updates on your booking status and driver readiness.
- **📜 Booking History:** Keep track of your active, completed, and cancelled trips with a detailed history view grouped by date.

---

## 🛠️ Technology Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **Backend:** [Firebase Firestore](https://firebase.google.com/products/firestore) (Real-time Database)
- **Authentication:** [Firebase Auth](https://firebase.google.com/products/auth) (Email & Password)
- **UI Framework:** [Android Jetpack](https://developer.android.com/jetpack)
  - **Navigation:** Fragment-based navigation with `nav_graph.xml`.
  - **View Binding:** Safe and efficient UI interaction.
  - **RecyclerView:** Optimized lists with multi-type adapters (Headers + Items).

---

## 🏗️ Architecture

The app follows the **Repository Pattern**, ensuring a clean separation between the UI and the data source (Firestore).

- **Data Layer:** `User.kt`, `Booking.kt`, and `Notification.kt` define the core data structures.
- **Logic Layer:** `Repository.kt` acts as the single source of truth for all Firebase operations and real-time listeners.
- **UI Layer:** Fragments manage screen-specific logic, while specialized Adapters (e.g., `SeatAdapter`, `BookingAdapter`) handle the complex grid and list renderings.

---

## 🚀 Getting Started

1. **Clone the Repo:**
   ```bash
   git clone https://github.com/NoelPOS/AUVan.git
   ```
2. **Setup Firebase:**
   - Create a project in the [Firebase Console](https://console.firebase.google.com/).
   - Add your `google-services.json` to the `app/` directory.
   - Enable Email/Password authentication and Firestore database.
3. **Build & Run:**
   - Open the project in **Android Studio**.
   - Click the "Run" button to deploy to an emulator or physical device.

---

## 👨‍💻 Developed By
**Noel POS**

---

> [!NOTE]
> This project was built with a focus on **clean code** and **user experience**, utilizing modern Android development best practices.
