# Hostel Manager — Mobile Application Development (MAD) Mini Project

**Hostel Manager** is a complete, native Android mobile application built using **Java**, **XML Layouts**, and embedded **SQLite Database**, developed in accordance with the Mobile Application Development course curriculum.

The application addresses everyday hostel operations by providing dedicated role portals for **Hostel Residents (Students)** and the **Hostel Warden / Administration**.

---

## 👥 Team Formation & Technical Roles

The project was executed by a collaborative team of three students with clear technical division:

| Team Member | Technical Role | Assigned Responsibilities & Modules Developed |
| :--- | :--- | :--- |
| **Student A** | **Team Leader & App Developer** | Overall application architecture, Activity lifecycle management, Student & Warden navigation flow, Room allocation logic, simulated fee payment engine, project integration, and hardware deployment. |
| **Student B** | **UI/UX Developer** | Material 3 UI design system, color palette (Hostel Navy `#1E3A8A` & Amber `#F59E0B`), XML screen layouts, responsive CardViews, vector drawables, status badges, and custom RecyclerView adapters (`ComplaintAdapter`, `RoomAdapter`, `FeeAdapter`, `StudentAdapter`). |
| **Student C** | **Database & Documentation Developer** | Normalized SQLite database schema (`HostelDbHelper.java`) with 4 relational tables, complete CRUD operations, atomic transactions, session management (`SessionManager.java`), input validations, 15-point functional test case matrix execution, and the final academic project report. |

---

## 🔑 Quick Demo Credentials (Pre-Configured)

The app comes pre-populated with realistic seed data so evaluators can test every feature immediately:

### 1. Student Portal
* **Email:** `student@hostel.com`
* **Password:** `student123`
* *(Name: Rahul Sharma, Roll No: CS2024-042, Room: 101-A)*
* **One-Tap Login:** Tap the **"AUTO-FILL"** chip on the login screen to automatically populate these credentials!

### 2. Hostel Warden / Admin Portal
* **Email:** `admin@hostel.com`
* **Password:** `admin123`
* *(Chief Warden Dr. K. Raman, Admin ID: ADMIN-01)*
* **One-Tap Login:** Tap the **"AUTO-FILL"** chip on the login screen to automatically populate these credentials!

### 3. Self-Registration
* Evaluators can also register brand new student residents via the **"Register here"** button.

---

## 🚀 Key Modules & Functional Features

### 🎓 1. Student Portal (`StudentMainActivity`)
* **Overview Dashboard:** Instant visual summary of allocated room, pending fee dues, active complaints count, and quick action shortcuts.
* **My Room:** Detailed specifications of the allocated room (Room Number, Block Wing, Floor, AC / Non-AC, Bed Capacity, and Current Occupancy). Displays fellow roommates residing in the same room and emergency warden contacts.
* **Complaints Management (CRUD):**
  * Raise maintenance tickets selecting from categories: *Plumbing, Electrical, Wi-Fi & Internet, Housekeeping, Carpentry, Mess / Food, Other*.
  * Select urgency levels (*Low, Medium, High*).
  * Filter complaints by status (*All, Pending, Resolved*).
  * Students can withdraw / delete their pending complaints if resolved informally.
  * View Warden resolution notes and timestamps once serviced.
* **Hostel Fee Management & Digital Receipt:**
  * Semester fee breakdown: Room Rent, Mess Charges, and Amenities / Maintenance.
  * Real-time balance due computation and payment status (*Paid, Partial, Unpaid*).
  * **Simulated Payment Gateway:** Pay fees via UPI (GPay/PhonePe), Debit/Credit Card, or Net Banking with instant transaction ID generation.
  * **Digital Receipt Viewer:** Generates an official hostel digital confirmation receipt with Receipt No, Transaction ID, Date, Amount, and Warden stamp.
* **Student Profile:** View personal details, branch roll number, contact information, and logout.

### 🛡️ 2. Warden / Admin Portal (`AdminMainActivity`)
* **Live KPI Metrics Grid:** Total Registered Students, Total Rooms, Active Unresolved Complaints, and Total Fee Revenue Collected.
* **Room Inventory Management (CRUD):**
  * Browse all rooms with real-time bed occupancy chips (*Vacant, Partially Occupied, Full*).
  * **Add Room:** Expand inventory by adding rooms with Block, Floor, Capacity, AC/Non-AC type, and semester rent.
  * **Allocate Student:** Assign an unallocated student directly to an available bed.
  * **Deallocate / Vacate:** Remove a student from a room, automatically updating room vacancy.
  * **Delete Room:** Remove obsolete rooms from the database.
* **Complaint Triage & Resolution:**
  * View complaints raised across the entire campus.
  * Filter by status (*All, Pending, Resolved*).
  * Update ticket status (*Pending -> In Progress -> Resolved*).
  * Enter official Warden resolution notes (e.g. *"Plumber visited and replaced faucet washer"*).
* **Fee Collection Ledger:**
  * Review payment records and balance dues of all hostel residents.
  * Record offline cash payments directly from the office.
* **Student Directory:**
  * Master directory of all residents with roll numbers, contact phone numbers, and allocated room numbers.

---

## 🛠️ Technology Stack

* **Platform:** Native Android (Min SDK: 24, Target/Compile SDK: 34)
* **Language:** Java (JDK 22 with Java 8 language level compatibility)
* **User Interface:** Material 3 Components, CardView, RecyclerView, ConstraintLayout, VectorDrawables
* **Local Database:** SQLite (`HostelDbHelper.java` via Android `SQLiteOpenHelper`)
* **Session Management:** Android `SharedPreferences`
* **Build System:** Gradle 8.7 & Android Gradle Plugin (AGP) 8.5.1
* **Tested & Verified On:** Physical Phone: **OnePlus CPH2381 (Android 14)** via ADB USB Debugging

---

## 📁 Repository Structure

```
mad_hostel-manager/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/hostel/manager/
│   │   │   ├── SplashActivity.java
│   │   │   ├── RoleSelectActivity.java
│   │   │   ├── LoginActivity.java
│   │   │   ├── RegisterActivity.java
│   │   │   ├── StudentMainActivity.java
│   │   │   ├── AddComplaintActivity.java
│   │   │   ├── AdminMainActivity.java
│   │   │   ├── AddRoomActivity.java
│   │   │   ├── HostelDbHelper.java       <-- SQLite CRUD Operations & Seed Data
│   │   │   ├── SessionManager.java       <-- SharedPreferences Auth State
│   │   │   ├── User.java                 <-- Data Model
│   │   │   ├── Room.java                 <-- Data Model
│   │   │   ├── Complaint.java            <-- Data Model
│   │   │   ├── FeeRecord.java            <-- Data Model
│   │   │   ├── ComplaintAdapter.java     <-- RecyclerView Adapter
│   │   │   ├── RoomAdapter.java          <-- RecyclerView Adapter
│   │   │   ├── FeeAdapter.java           <-- RecyclerView Adapter
│   │   │   └── StudentAdapter.java       <-- RecyclerView Adapter
│   │   └── res/
│   │       ├── layout/                   <-- XML Screen Layouts & Custom Dialogs
│   │       ├── drawable/                 <-- Vector Icons & Badges
│   │       └── values/                   <-- Colors, Strings, Material3 Theme
│   └── build.gradle.kts
├── Hostel_Manager_Mini_Project_Report.docx <-- Academic Submission Word Report
├── screen_01_role_select.png               <-- Real Phone Screenshot
├── screen_04_student_dashboard.png         <-- Real Phone Screenshot
├── screen_07_student_fees.png              <-- Real Phone Screenshot
├── screen_09_admin_rooms.png               <-- Real Phone Screenshot
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew.bat
└── README.md
```

---

## 📄 Academic Project Report (.docx)

A complete university-format project report has been compiled and saved as:
**`Hostel_Manager_Mini_Project_Report.docx`**

The report includes:
1. Cover Page & Group Technical Responsibility Breakdown
2. Certificate of Authenticity & Student Declaration
3. Abstract
4. Introduction, Problem Statement & Objectives
5. Literature & Existing System Study
6. Functional & Non-Functional Requirements Matrix
7. Technology Stack Selection & Justification
8. System Architecture Diagram
9. Database Design & Data Dictionaries (users, rooms, complaints, fees)
10. Implementation & Class Breakdown
11. **Application Screenshots** (All 13 high-resolution screenshots captured directly from the physical phone)
12. 15-Point Functional Test Case Matrix (All Passed)
13. Challenges Encountered & Technical Solutions
14. Individual Student Contributions (Student A, Student B, Student C)
15. Conclusion, Future Enhancements, and Academic References
