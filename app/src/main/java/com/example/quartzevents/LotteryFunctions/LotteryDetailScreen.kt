package com.example.quartzevents.LotteryFunctions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quartzevents.Classes.Lottery
import com.example.quartzevents.EntrantFunctions.registrationIsOpen
import com.example.quartzevents.Repositories.CampsiteInfoRepository
import com.example.quartzevents.Repositories.LotteryRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LotteryDetailScreen(lotteryId: String, userId: String, onBack: () -> Unit) {
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

    BackHandler(onBack = onBack)
    Scaffold(topBar = {
        TopAppBar(title = { Text("Lottery details") }, navigationIcon = {
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
                    Text(draw.name, style = MaterialTheme.typography.headlineSmall)
                    Text("Registration: " + draw.registrationStart + " to " + draw.registrationEnd)
                    Text("Booking: " + draw.bookingStart + " to " + draw.bookingEnd)
                    val sites = remember { CampsiteInfoRepository() }
                    DisposableEffect(sites) { onDispose { sites.stopListening() } }
                    val names = sites.campsites.filter { it.id in draw.campsites }.map { it.name }
                    if (names.isNotEmpty()) Text("Campsites: " + names.joinToString())
                    val joined = userId in draw.entrants
                    val open = draw.drawnAt == null && draw.winners.isEmpty() &&
                        registrationIsOpen(draw.registrationStart, draw.registrationEnd, LotteryRepository.today())
                    LotteryStatusScreen(draw, userId, open)
                    if (open) {
                        val full = draw.capacity?.let { draw.entrants.size >= it } == true
                        Button(enabled = !saving && (joined || !full), onClick = {
                            saving = true
                            error = null
                            scope.launch {
                                try {
                                    LotteryRepository.setMembership(lotteryId, userId, !joined)
                                } catch (e: CancellationException) {
                                    throw e
                                } catch (e: Exception) {
                                    error = e.localizedMessage ?: "Could not update the waitlist."
                                } finally {
                                    saving = false
                                }
                            }
                        }) {
                            Text(when {
                                joined -> "Leave waitlist"
                                full -> "Waitlist full"
                                else -> "Join waitlist"
                            })
                        }
                    }
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}
