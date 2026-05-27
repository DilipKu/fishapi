# Rio Fisheries - Management App

A professional, robust Android application designed to streamline fisheries operations, including hunter management, catch tracking, sales, and expense logging. Built with **Jetpack Compose** and powered by **Supabase**.

---

## 🚀 Features

### 🛡️ Authentication & Security
- **Persistent Sessions**: Log in once and stay logged in until you choose to sign out.
- **Remember Me**: Saves credentials locally using SharedPreferences for faster access.
- **Branded Launch**: Modern system-integrated Splash Screen with the Rio Fisheries logo.

### 🏠 Dashboard
- **Dynamic UI**: Clean, Material 3 dashboard with quick-action cards.
- **Banner Slider**: Auto-scrolling branded banners highlighting key business activities.

### 🎣 Business Operations
- **Hunter Management**: Register hunters with multi-category expertise and **customized pricing (Rates)**.
- **Batch Catch Entry**: Smart form for recording multiple fish categories at once for a single hunter.
- **Automated Billing**: Automatically calculates total catch price based on the hunter's registered rates.
- **Sales Tracking**: Log sales data with weights, pricing, and specific remarks.
- **Expense Logging**: Track business costs (Transport, Fisherman pay, Company costs, etc.) with detailed descriptions.

### 🌍 Localization & UX
- **Bilingual Support**: Full support for **English** and **Hindi (हिंदी)** languages.
- **Real-time Refresh**: Lists automatically update and sort to show the **most recent entries at the top**.
- **IST Timezone**: All database timestamps are automatically converted to **Indian Standard Time (IST)** for accuracy.
- **High Readability**: Custom high-contrast typography with enlarged bold fonts for outdoor usage.

---

## 🛠 Tech Stack

- **UI**: Jetpack Compose (100% Declarative)
- **Design System**: Material 3
- **Navigation**: Compose Navigation
- **Architecture**: MVVM (Model-View-ViewModel)
- **Backend**: Supabase (PostgreSQL + Auth)
- **Local Storage**: Room (SQLite) with Offline-First architecture
- **Networking**: Ktor Client
- **Security**: SharedPreferences for credentials
- **Concurrency**: Kotlin Coroutines & Flow

---

## 📂 Project Structure

```
com.example.composeapp
├── data
│   ├── model       # FisheryModels (Hunter, Catch, Sale, Expense)
│   └── remote      # SupabaseClient & API configuration
├── ui
│   ├── components  # Reusable UI elements (Dropdowns, Error displays, Logo)
│   ├── screens     # Feature-based screen modules (Auth, Home, Features)
│   └── theme       # Typography, Color schemes, and Theme definition
└── viewmodel       # Business logic (AuthViewModel, FishViewModel)
```

---

## ⚙️ Setup & Installation

1. **Clone the project**:
   ```bash
   git clone https://github.com/your-username/RioFisheries.git
   ```

2. **Supabase Configuration**:
   - Create a project on [Supabase](https://supabase.com/).
   - Run the provided SQL scripts (see `docs/db_schema.sql`) in the SQL Editor.
   - Update `gradle.properties` with your `SUPABASE_URL` and `SUPABASE_KEY`.

3. **Build**:
   - Open in Android Studio (Ladybug or newer).
   - Sync Gradle and run the `:app` module.

---

## 📝 License
Copyright © 2024 Rio Fisheries. All rights reserved.
