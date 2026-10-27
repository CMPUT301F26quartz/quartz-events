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
import androidx.compose.ui.graphics.Color


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


        // Show screen based on user role
        else -> {
            RoleHomeScreen(
                profile = currentProfile!!,
                onSearch = {
                    isSearchScreenVisible = true
                },
                onLogout = {
                    repository.logout()
                    isSearchScreenVisible = false
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
    onLogout: () -> Unit
) {
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
// The future entrant dashboard can reuse this search entry.
@Composable
private fun SearchCampsitesButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF254D3C),
            contentColor = Color.White
        )
    ) {
        Text(text = "Search Campsites")
    }
}