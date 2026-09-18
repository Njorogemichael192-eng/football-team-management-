# Football Team Manager

A small Android application for managing football team players through a Kotlin Ktor REST API backed by MongoDB Atlas.

## Features

- View all players in a Material 3 Compose list
- Search players by name, case-insensitively
- Add, view, edit, and delete players
- Validate player data before saving
- REST API with appropriate CRUD status codes

## Technology stack

- Android, Kotlin, Jetpack Compose, Material 3
- Ktor and Kotlin serialization
- MongoDB Atlas
- Gradle Kotlin DSL

## Project structure

```text
football-team-management/
├── android-app/       Android Compose application
├── backend/            Ktor REST API
├── PROJECT_STATE.md
├── README.md
└── .gitignore
```

## MongoDB setup

Create a MongoDB Atlas cluster and a database named `football_team`. The backend uses the `players` collection and creates it when needed.

Set the connection string in the environment before starting the backend:

```bash
MONGODB_URI="mongodb+srv://user:password@cluster.example.mongodb.net/?retryWrites=true&w=majority"
```

The URI is never stored in the Android application.

## Run the backend

From the `backend` directory:

```bash
# Linux/macOS/GitHub Codespaces
export MONGODB_URI="your-mongodb-atlas-uri"
./gradlew run

# Windows PowerShell
$env:MONGODB_URI = "your-mongodb-atlas-uri"
./gradlew.bat run
```

The API listens on `http://localhost:8080`.

## Open the Android project

Open `android-app` in Android Studio, allow Gradle sync to finish, and run the `app` configuration on an emulator or device. The default emulator API base URL is `http://10.0.2.2:8080/`. For a physical device, update `API_BASE_URL` in `android-app/app/build.gradle.kts` to the host machine's LAN address.

## Build the Android APK

From `android-app`:

```bash
./gradlew assembleDebug
```

On Windows PowerShell, use `./gradlew.bat assembleDebug`. The debug APK is created under `app/build/outputs/apk/debug/`.
