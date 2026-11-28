package com.example.quartzevents.Messaging

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quartzevents.Repositories.MessageRepo
import com.example.quartzevents.Repositories.RecipientOption
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

@OptIn(ExperimentalUuidApi::class)
@Composable
internal fun ComposeMail(uid: String, owner: Boolean, replyTo: String?, onAddCampsite: () -> Unit,
                        onBack: () -> Unit, onSent: (Int) -> Unit) {
    var kind by rememberSaveable { mutableStateOf(if (owner && replyTo == null) "campsite" else "reservation") }
    var id by rememberSaveable { mutableStateOf(replyTo.orEmpty()) }
    var site by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }
    var requestId by rememberSaveable { mutableStateOf("") }
    var retry by remember { mutableIntStateOf(0) }
    var targets by remember(uid, kind, retry) { mutableStateOf<List<RecipientOption>?>(null) }
    var loadError by remember(uid, kind, retry) { mutableStateOf<String?>(null) }
    var menu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var sending by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(uid, owner, kind, retry) {
        try {
            targets = MessageRepo.recipientOptions(uid, owner, kind)
        } catch (failure: FirebaseFirestoreException) {
            loadError = when (failure.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Your account cannot read these recipients."
                FirebaseFirestoreException.Code.UNAVAILABLE -> "Firestore is unavailable. Check your connection and retry."
                else -> "Could not load recipients (${failure.code})."
            }
        }
    }
    val choices = targets.orEmpty()
    val selected = choices.find { it.id == id && it.site == site }

    BackHandler { if (!sending) onBack() }
    Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding().verticalScroll(rememberScrollState())
        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onBack, enabled = !sending) { Text("Back") }
        Text("New message", style = MaterialTheme.typography.headlineMedium)
        if (owner) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for ((value, label) in listOf("campsite" to "Campsite", "waitlist" to "Lottery", "reservation" to "Reservation")) {
                    FilterChip(kind == value, enabled = !sending, onClick = {
                        kind = value; id = ""; site = ""; requestId = ""; error = ""
                    }, label = { Text(label) })
                }
            }
        }
        when {
            loadError != null -> {
                Text(loadError.orEmpty())
                TextButton(onClick = { retry++ }) { Text("Retry") }
            }
            targets == null -> CircularProgressIndicator()
            choices.isEmpty() -> {
                Text(when (kind) {
                    "campsite" -> "You have no campsites yet."
                    "waitlist" -> "No lotteries are linked to your campsites."
                    else -> "No reservations with a linked contact yet."
                })
                if (owner && kind == "campsite") TextButton(onClick = onAddCampsite) { Text("Add campsite") }
            }
            else -> {
                Text(when (kind) {
                    "campsite" -> "To everyone on this campsite’s lottery waitlists"
                    "waitlist" -> "To entrants on one lottery waitlist"
                    else -> "To your reservation contact"
                })
                Box {
                    OutlinedButton(enabled = !sending, onClick = { menu = true }) {
                        Text(selected?.label ?: "Choose recipient")
                    }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        choices.forEach { choice ->
                            DropdownMenuItem(text = { Text(choice.label) }, onClick = {
                                id = choice.id; site = choice.site; menu = false; requestId = ""; error = ""
                            })
                        }
                    }
                }
            }
        }
        OutlinedTextField(body, {
            if (it.length <= 2000) { body = it; requestId = ""; error = "" }
        }, Modifier.fillMaxWidth(), enabled = !sending, label = { Text("Message") }, minLines = 4, maxLines = 8)
        if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
        Button(enabled = selected != null && body.isNotBlank() && !sending, onClick = {
            val recipient = selected ?: return@Button
            if (requestId.isEmpty()) requestId = Uuid.random().toString()
            sending = true
            error = ""
            scope.launch {
                try {
                    val count = if (kind == "reservation") MessageRepo.sendReservationMessage(id, requestId, body)
                        else MessageRepo.sendWaitlistNotice(recipient.site, recipient.id.takeIf { kind == "waitlist" }, requestId, body)
                    onSent(count)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (failure: Exception) {
                    error = MessageRepo.sendError(failure)
                } finally {
                    sending = false
                }
            }
        }) { Text(if (sending) "Sending…" else "Send") }
    }
}
