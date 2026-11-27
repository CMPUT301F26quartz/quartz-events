package com.example.quartzevents

/**
 * Main entry point of the application.
 * Initializes Firebase and launches the app UI.
 */
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
import com.example.quartzevents.Classes.Date
import com.example.quartzevents.EntrantFunctions.EntrantSearchScreen
import com.example.quartzevents.OwnerFunctions.LotteryCreationScreen1
import com.example.quartzevents.OwnerFunctions.LotteryCreationScreen2
import com.example.quartzevents.OwnerFunctions.LotteryUpdateScreen1
import com.example.quartzevents.OwnerFunctions.LotteryUpdateScreen2
import com.example.quartzevents.OwnerFunctions.OwnerLotteryHomeScreen
import com.example.quartzevents.EntrantFunctions.EntrantHomeScreen
import com.example.quartzevents.LotteryFunctions.ActiveLotteriesScreen
import com.example.quartzevents.OwnerFunctions.OwnerHomeScreen
import com.example.quartzevents.OwnerFunctions.RegisterCampsite
import com.example.quartzevents.OwnerFunctions.CampsiteDetails
import com.example.quartzevents.OwnerFunctions.LotteryNonOwnerUpdateScreen
import com.example.quartzevents.Repositories.LotteryRepository
import com.example.quartzevents.Repositories.UserRepoMain
import java.time.LocalDate
import java.time.ZoneOffset


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

    val lotRepo = remember { LotteryRepository() }

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
            //CheckLotteries()
            AdminDashboardScreen(onLogout = {
                repository.logout()
                currentProfile = null
            })
        }

        currentProfile?.role == "entrant" && isSearchScreenVisible -> {
            //CheckLotteries()
            EntrantSearchScreen(
                currentProfile!!,
                onBack = {
                    isSearchScreenVisible = false
                }
            )
        }


        // Show screen based on user role
        //main owner switchboard
        else -> {


            if (currentProfile?.role == "owner") {
                //CheckLotteries()
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

/**
 * Checks the database to see if any of the lotteries have reached draw date,
 * then calls the selectWinner function from the lottery class to
 * start the lottery winner process if today is equal to or after the end of
 * registration.
 */
@Composable
private fun CheckLotteries(){
    val lotRepo = remember { LotteryRepository() }
    val lotteries = lotRepo.getAllLotteries()
    val userRepo = remember { UserRepoMain() }
    val users = userRepo.getAllUsers()
    val today = Date(LocalDate.now(ZoneOffset.UTC).toString()) // get current date in UTC
    if (lotteries.isNotEmpty()) {
        lotteries.forEach { item -> // check each lottery in the collection
            if (Date(item.registrationEnd).isEqual(today) || Date(item.registrationEnd).isAfter(today)) {
                if (item.creatorId != null) {
                    lotRepo.selectWinner(item.creatorId,item)
                    val lottery = lotRepo.getLottery(item.id!!)
                    users.forEach { user ->
                        if (lottery?.winners?.contains(user.uid) == true ) {
                            val winNotif = lottery.getWinNotif(user.uid, item.creatorId)
                        } else if (lottery?.entrants?.contains(user.uid) == true) {
                            val loseNotif = lottery.getLoseNotif(user.uid,item.creatorId)
                        }
                    }

                } else {
                    throw Error("Could Not Parse Owner Id")
                }
            }
        }
    } else {
        throw Error("No Lotteries Found.")
    }
}
