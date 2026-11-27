package com.example.quartzevents

/**
 * Main entry point of the application.
 * Initializes Firebase and launches the app UI.
 */
import kotlinx.coroutines.tasks.await
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
import com.example.quartzevents.Repositories.AuthRepository
import com.example.quartzevents.auth.AuthScreen
import com.example.quartzevents.Messaging.MessageCenter
import com.example.quartzevents.model.UserProfile
import com.example.quartzevents.ui.theme.QuartzEventsTheme
import com.google.firebase.FirebaseApp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import com.example.quartzevents.AdminFunctions.AdminDashboardScreen
import com.example.quartzevents.EntrantFunctions.EntrantSearchScreen
import com.example.quartzevents.OwnerFunctions.LotteryCreationScreen1
import com.example.quartzevents.OwnerFunctions.LotteryCreationScreen2
import com.example.quartzevents.OwnerFunctions.LotteryUpdateScreen1
import com.example.quartzevents.OwnerFunctions.LotteryUpdateScreen2
import com.example.quartzevents.OwnerFunctions.OwnerLotteryHomeScreen
import com.example.quartzevents.EntrantFunctions.EntrantHomeScreen
import com.example.quartzevents.OwnerFunctions.OwnerHomeScreen
import com.example.quartzevents.OwnerFunctions.RegisterCampsite
import com.example.quartzevents.OwnerFunctions.CampsiteDetails
import com.example.quartzevents.OwnerFunctions.LotteryNonOwnerUpdateScreen
import com.example.quartzevents.Repositories.LotteryRepository


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

/**
 * Manages authentication and displays screens based on the user's role.
 */
@Composable
fun QuartzEventsApp() {
    val repository = remember { AuthRepository() }
    var currentProfile by remember {
        mutableStateOf<UserProfile?>(null)
    }
    var isLoading by remember {
        mutableStateOf(true)
    }
    var isSearchScreenVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var isCreatingCampsite by rememberSaveable {
        mutableStateOf(false)
    }
    var isViewingLottery by rememberSaveable {
        mutableStateOf(false)
    }
    var isCreatingLottery1 by rememberSaveable {
        mutableStateOf(false)
    }
    var isCreatingLottery2 by rememberSaveable {
        mutableStateOf(false)
    }
    var isUpdatingLottery1 by rememberSaveable {
        mutableStateOf(false)
    }
    var isUpdatingLottery2 by rememberSaveable {
        mutableStateOf(false)
    }

    var showMessages by rememberSaveable { mutableStateOf(false) }

    var selectedCampsiteId by rememberSaveable { mutableStateOf<String?>(null) }

    var selectedLotteryId by rememberSaveable { mutableStateOf<String?>(null) }

    var selectedSitesForLottery by rememberSaveable { mutableStateOf<String?>(null) }

    var selectedSitesForUpdatingLottery by rememberSaveable { mutableStateOf<String?>(null) }


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

        showMessages -> MessageCenter(currentProfile!!, onBack = { showMessages = false }, onAddCampsite = {
            showMessages = false
            isCreatingCampsite = true
        })
        currentProfile?.role == "admin" -> {
            AdminDashboardScreen(onLogout = {
                repository.logout()
                currentProfile = null
            })
        }

        currentProfile?.role == "entrant" && isSearchScreenVisible -> {
            EntrantSearchScreen(
                profile = currentProfile!!,
                onBack = {
                    isSearchScreenVisible = false
                }
            )
        }


        // Show screen based on user role
        //main owner switchboard
        else -> {


            if (currentProfile?.role == "owner") {
                val ownerId = currentProfile!!.uid
                val lotRepo = remember(ownerId) { LotteryRepository() }
                DisposableEffect(lotRepo) { onDispose { lotRepo.stopListening() } }
                val today = LotteryRepository.today()
                val due = lotRepo.getAllLotteries().filter { it.creatorId == ownerId &&
                    it.drawnAt == null && it.registrationEnd.isNotEmpty() && it.registrationEnd < today }
                var drawError by remember(ownerId) { mutableStateOf<String?>(null) }
                var retry by remember { mutableIntStateOf(0) }
                LaunchedEffect(ownerId, due.map { it.id }, retry) {
                    drawError = null
                    for (lottery in due) {
                        try {
                            lotRepo.selectWinner(ownerId, lottery).await()
                        } catch (error: com.google.firebase.firestore.FirebaseFirestoreException) {
                            drawError = "Could not finish the draw for " + lottery.name
                            Log.e("LotteryDraw", "Draw failed", error)
                        }
                    }
                }
                if (drawError != null) {
                    AlertDialog(onDismissRequest = { drawError = null }, text = { Text(drawError!!) },
                        confirmButton = { TextButton(onClick = { retry++ }) { Text("Retry") } },
                        dismissButton = { TextButton(onClick = { drawError = null }) { Text("Close") } })
                }
                if (selectedCampsiteId != null) {
                    CampsiteDetails(
                        campsiteId = selectedCampsiteId!!,
                        onBack = { selectedCampsiteId = null }
                    )
                } else if (isCreatingCampsite) {
                    RegisterCampsite(
                        onBack = { isCreatingCampsite = false },
                        onSaveSuccess = { isCreatingCampsite = false }
                    )

                }else if (isViewingLottery) {
                    OwnerLotteryHomeScreen(
                        currentProfile!!,
                        onCreateLottery = { isCreatingLottery1 = true; isViewingLottery = false },
                        onLotterySelect = { id -> selectedLotteryId = id },
                        onUpdateLottery = { isUpdatingLottery1 = true; isViewingLottery = false },
                        onBack = { isViewingLottery = false }
                    )

                } else if (isCreatingLottery1) {
                    LotteryCreationScreen1(
                        profile = currentProfile!!,
                        onSelectSites = {sites -> selectedSitesForLottery = sites; isCreatingLottery2 = true; isCreatingLottery1 = false},
                        onBack = { isCreatingLottery1 = false; isViewingLottery = true }
                    )
                } else if (selectedSitesForLottery != null && isCreatingLottery2) {
                    LotteryCreationScreen2(
                        currentProfile!!,
                        selectedSites = selectedSitesForLottery,
                        onCreation = {selectedSitesForLottery = null; isCreatingLottery2 = false},
                        onBack = { selectedSitesForLottery = null; isCreatingLottery2 = false; isCreatingLottery1 = true }
                    )

                } else if (selectedLotteryId != null && isUpdatingLottery1) {
                    val lottery = lotRepo.getLottery(selectedLotteryId!!)
                    if (lottery?.creatorId == currentProfile!!.uid) {
                        LotteryUpdateScreen1(
                            profile = currentProfile!!,
                            selectedLotteryId,
                            onSelectSites = { sites ->
                                selectedSitesForUpdatingLottery = sites
                                isUpdatingLottery2 = true
                                isUpdatingLottery1 = false
                            },
                            onBack = {
                                selectedLotteryId = null
                                isUpdatingLottery1 = false
                                isViewingLottery = true
                            }
                        )
                    } else {
                        LotteryNonOwnerUpdateScreen(
                            currentProfile!!,
                            selectedLotteryId,
                            onUpdate = {
                                isUpdatingLottery1 = false
                                selectedLotteryId = null
                            },
                            onBack = {
                                isUpdatingLottery1 = false
                                selectedLotteryId = null
                                isViewingLottery = true
                            }
                        )
                    }
                } else if (selectedLotteryId != null && isUpdatingLottery2) {
                    val sites = selectedSitesForUpdatingLottery!!.split(" ")
                    LotteryUpdateScreen2(
                        selectedSites = sites as MutableList<String?>,
                        selectedLotteryId,
                        onUpdate = {selectedLotteryId = null; isUpdatingLottery2 = false},
                        onBack = { isUpdatingLottery2 = false; isUpdatingLottery1 = true }
                    )
                } else {
                    OwnerHomeScreen(
                        profile = currentProfile!!,
                        onMessages = { showMessages = true },
                        onAddCampsiteClick = { isCreatingCampsite = true },
                        onViewDetailsClick = { id -> selectedCampsiteId = id },
                        onViewLotteriesClick = { isViewingLottery = true },
                        onLogout = {
                            repository.logout()
                            currentProfile = null
                        }
                    )
                }

            } else {

                RoleHomeScreen(
                    profile = currentProfile!!,
                    onMessages = { showMessages = true },
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
}

/**
 * Displays the home screen for the logged-in user.
 */
@Composable
fun RoleHomeScreen(
    profile: UserProfile,
    onSearch: () -> Unit,
    onMessages: () -> Unit,
    onLogout: () -> Unit
) {
    if (profile.role == "entrant") {
        EntrantHomeScreen(profile, onSearch, onLogout, onMessages)
        return
    }

    val roleName = when (profile.role) {
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

        if (profile.role == "owner") {
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
