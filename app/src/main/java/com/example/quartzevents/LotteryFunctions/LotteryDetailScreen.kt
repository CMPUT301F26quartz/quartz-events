package com.example.quartzevents.LotteryFunctions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import com.example.quartzevents.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.quartzevents.Classes.Campsite
import com.example.quartzevents.Classes.Lottery
import com.example.quartzevents.EntrantFunctions.registrationIsOpen
import com.example.quartzevents.Repositories.CampsiteInfoRepository
import com.example.quartzevents.Repositories.LotteryRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LotteryDetailScreen(lotteryId: String, userId: String, onBack: () -> Unit) {
    var viewEntry by rememberSaveable(lotteryId, userId) { mutableStateOf(false) }
    LotteryEntryPage(lotteryId, userId, viewEntry,
        onBack = { if (viewEntry) viewEntry = false else onBack() },
        onViewEntry = { viewEntry = true }, onLeft = { viewEntry = false })
}

@Composable
fun LotteryDetailScreen(lotteryId: String, userId: String, onAddClick: () -> Unit, onBack: () -> Unit) {
    LotteryEntryPage(lotteryId, userId, false, onBack, onViewEntry = onAddClick, onLeft = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LotteryEntryPage(
    lotteryId: String, userId: String, viewEntry: Boolean, onBack: () -> Unit,
    onViewEntry: () -> Unit, onLeft: () -> Unit
) {
    var lottery by remember(lotteryId) { mutableStateOf<Lottery?>(null) }
    var loading by remember(lotteryId) { mutableStateOf(true) }
    var error by remember(lotteryId) { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    var retry by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    DisposableEffect(lotteryId, retry) {
        loading = true
        error = null
        val listener = Firebase.firestore.collection("Lotteries").document(lotteryId)
            .addSnapshotListener { doc, failure ->
                loading = false
                lottery = doc?.takeIf { it.exists() }?.let { LotteryRepository.readLottery(it) }
                error = if (failure != null) "Could not load this lottery." else null
            }
        onDispose { listener.remove() }
    }

    val afterJoin by rememberUpdatedState(onViewEntry)
    val afterLeave by rememberUpdatedState(onLeft)
    BackHandler(onBack = onBack)
    Scaffold(topBar = {
        TopAppBar(title = { Text(if (viewEntry) "Lottery Status" else "Lottery details") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val draw = lottery
            when {
                loading -> CircularProgressIndicator()
                draw == null -> {
                    Text(error ?: "This lottery is no longer available.")
                    if (error != null) TextButton(onClick = { retry++ }) { Text("Retry") }
                }
                else -> {
                    val sites = remember { CampsiteInfoRepository() }
                    DisposableEffect(sites) { onDispose { sites.stopListening() } }
                    val linkedSites = sites.campsites.filter { it.id in draw.campsites }
                    val joined = userId in draw.entrants
                    val open = draw.drawnAt == null && draw.winners.isEmpty() &&
                        registrationIsOpen(draw.registrationStart, draw.registrationEnd, LotteryRepository.today())
                    fun updateMembership(join: Boolean) {
                        saving = true
                        error = null
                        scope.launch {
                            try {
                                LotteryRepository.setMembership(lotteryId, userId, join).await()
                                if (join) afterJoin() else afterLeave()
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                error = e.localizedMessage ?: "Could not update the waitlist."
                            } finally {
                                saving = false
                            }
                        }
                    }
                    if (viewEntry) {
                        LotteryStatusContent(draw, userId, linkedSites, open, saving,
                            onLeave = { updateMembership(false) })
                    } else {
                        Text(draw.name, style = MaterialTheme.typography.headlineSmall)
                        Text("Registration: " + draw.registrationStart + " to " + draw.registrationEnd)
                        Text("Booking: " + draw.bookingStart + " to " + draw.bookingEnd)
                        Text("Included Campsites (" + draw.campsites.size + ")", style = MaterialTheme.typography.titleMedium)
                        for (site in linkedSites) CampDisplay(site.name, site.location)
                        if (sites.errorMessage != null) Text(sites.errorMessage!!)
                        val full = draw.isWaitlistFull()
                        Text("Status: " + when {
                            !open -> "Registration closed"
                            full -> "Waitlist full"
                            else -> "Open for Entry"
                        })
                        if (joined || userId in draw.winners) {
                            Button(onClick = onViewEntry) { Text("View my entry") }
                        } else if (open) {
                            Button(enabled = !saving && !full, onClick = { updateMembership(true) }) {
                                Text(if (full) "Waitlist full" else "Join waitlist")
                            }
                        } else {
                            Text("Registration is closed.")
                        }
                    }
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@Composable
fun CampDisplay(
    campsite: Campsite
) {
    CampDisplay(campsite.name, campsite.location)
}

@Composable
private fun CampDisplay(name: String, location: String) {
    Box (
        modifier = Modifier
            .size(350.dp,65.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(20.dp))
    ) {
        Row (
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    name,
                    fontSize = 23.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Text(
                    location,
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
            Icon(
                painter = painterResource(R.drawable.chevron_right),
                contentDescription = "arrow icon",
                modifier = Modifier.padding(start = 5.dp),
                tint = Color.Gray
            )
        }
    }
}
