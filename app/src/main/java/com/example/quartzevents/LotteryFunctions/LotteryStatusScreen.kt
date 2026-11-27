package com.example.quartzevents.LotteryFunctions

import android.text.format.DateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quartzevents.Classes.Lottery
import com.example.quartzevents.EntrantFunctions.CampsiteInfo

/** Standalone entry point for callers opening a participant's status directly. */
@Composable
fun LotteryStatusScreen(lotteryId: String, userId: String, onBack: () -> Unit) {
    LotteryEntryPage(lotteryId, userId, viewEntry = true, onBack = onBack,
        onViewEntry = {}, onLeft = onBack)
}

@Composable
internal fun LotteryStatusContent(
    lottery: Lottery, userId: String, campsites: List<CampsiteInfo>,
    registrationOpen: Boolean, saving: Boolean, onLeave: () -> Unit
) {
    val joined = userId in lottery.entrants
    val finished = lottery.drawnAt != null || lottery.winners.isNotEmpty()
    val status = when {
        userId in lottery.winners -> "You won this lottery."
        !joined -> "You are not on the waitlist."
        finished -> "You were not selected."
        registrationOpen -> "Waiting"
        else -> "Registration closed. Waiting for the draw."
    }
    Text("My Lottery Entry", style = MaterialTheme.typography.headlineSmall)
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(lottery.name, style = MaterialTheme.typography.titleMedium)
            for (site in campsites) Text(listOf(site.name, site.location).filter { it.isNotBlank() }.joinToString(" / "))
            if (joined && !finished) Text("Joined Waiting List")
            Text("Registration ends: " + lottery.registrationEnd)
            lottery.drawnAt?.let { Text("Draw date: " + DateFormat.format("yyyy-MM-dd", it.toDate())) }
        }
    }
    Text("Entry Details", style = MaterialTheme.typography.titleLarge)
    Text("Entry Status: " + status)
    if (joined || userId in lottery.winners) {
        Text(if (finished) "Check Messages for your lottery result."
            else "Your result will appear in Messages after the draw.")
    }
    if (joined && registrationOpen) {
        OutlinedButton(onClick = onLeave, enabled = !saving) { Text("Leave Waiting List") }
    }
}
