package com.example.quartzevents

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quartzevents.auth.AuthRepository
import com.example.quartzevents.auth.AuthScreen
import com.example.quartzevents.model.UserProfile
import com.example.quartzevents.ui.theme.QuartzEventsTheme
import com.google.firebase.FirebaseApp
import androidx.compose.runtime.saveable.rememberSaveable


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase
        val firebaseApp = FirebaseApp.initializeApp(this)
        if (firebaseApp != null) {
            Log.d("QuartzFirebase", "Firebase initialized successfully")
        } else {
            Log.e("QuartzFirebase", "Firebase initialization failed")
        }
        enableEdgeToEdge()
        setContent {
            QuartzEventsTheme {
                QuartzEventsApp()
            }
        }
    }
}

@Composable
fun QuartzEventsApp() {
    val repository = remember { AuthRepository() }
    var currentProfile by remember {
        mutableStateOf<UserProfile?>(null)
    }
    var isLoading by remember {
        mutableStateOf(true)
    }
    // TODO: Replace with shared navigation when the dashboard is ready.
    var isSearchScreenVisible by rememberSaveable {
        mutableStateOf(false)
    }

    // tracks which profile-related screen is currently displayed.
    var profileScreen by rememberSaveable {
        mutableStateOf("home")
    }

    // Temporary notification state until user preferences are integrated
    var notificationsEnabled by rememberSaveable {
        mutableStateOf(true)
    }

    //Check if a user is already signed in
    LaunchedEffect(Unit) {
        val uid = repository.getCurrentUserId()
        if (uid != null) {
            repository.getUserProfile(uid) { profile, _ ->
                currentProfile = profile
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }
    when {
        // Show loading indicator
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Show Login/signup screen
        currentProfile == null -> {
            AuthScreen(
                onAuthenticated = { profile ->
                    isSearchScreenVisible = false
                    currentProfile = profile
                }
            )
        }

        currentProfile?.role == "entrant" && isSearchScreenVisible -> {
            EntrantSearchScreen(
                onBack = {
                    isSearchScreenVisible = false
                }
            )
        }

        currentProfile?.role == "entrant" && profileScreen == "profile" -> {
            ProfileScreen(
                profile = currentProfile!!,
                enableNotification = notificationsEnabled,
                onNotificationsChanged = { enabled ->
                    notificationsEnabled = enabled
                },
                onEditProfile = { profileScreen = "edit_profile" },
                onViewPolicies = { profileScreen = "policies" },
                onDeleteProfile = { }, // TODO: connect DeleteProfileScreen Later
                onBack = { profileScreen = "home" }
            )
        }

        currentProfile?.role == "entrant" && profileScreen == "policies" -> {
            PoliciesScreen(
                onBack = {
                    profileScreen = "profile"
                }
            )
        }

        currentProfile?.role == "entrant" && profileScreen == "edit_profile" -> {
            EditProfileScreen(
                profile = currentProfile!!,
                onBack = { profileScreen = "profile" },
                onSave = { updatedProfile ->
                    // TODO: save updated profile to Firebase
                    currentProfile = updatedProfile
                    profileScreen = "profile"
                }
            )
        }

        currentProfile?.role == "admin" -> {
            AdminDashboardScreen(onLogout = {
                repository.logout()
                currentProfile = null
            })
        }


        // Show screen based on user role
        else-> {
            RoleHomeScreen(
                profile = currentProfile!!,
                onSearch = {
                    isSearchScreenVisible = true
                },
                onProfile = {
                    profileScreen = "profile"
                },
                onLogout = {
                    repository.logout()
                    isSearchScreenVisible = false
                    profileScreen = "home"
                    currentProfile = null
                }
            )
        }
    }
}

@Composable
fun RoleHomeScreen(
    profile: UserProfile,
    onSearch: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit
) {
    if (profile.role == "entrant") {
        EntrantHomeScreen(profile, onSearch, onLogout)
        return
    }

    val roleName = when (profile.role) {
        "entrant" -> "Entrant"
        "owner" -> "Campsite Owner"
        "admin" -> "Admin"
        else -> "Unknown"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome, ${profile.name}!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (profile.role == "entrant") {
            SearchCampsitesButton(onClick = onSearch)

            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "Role: $roleName"
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLogout) {
            Text("Log Out")
        }
    }
}
