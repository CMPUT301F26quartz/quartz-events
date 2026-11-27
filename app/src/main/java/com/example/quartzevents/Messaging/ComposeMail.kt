package com.example.quartzevents.Messaging

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.example.quartzevents.Classes.Message
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
internal fun ComposeMail(uid: String, owner: Boolean, replyTo: Message?, onAddCampsite: () -> Unit,
                        onBack: () -> Unit, onSent: (Int) -> Unit) {
    var kind by rememberSaveable { mutableStateOf(if (owner && replyTo == null) "campsite" else "reservation") }
    var id by rememberSaveable { mutableStateOf("") }
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

    LaunchedEffect(uid, owner, kind, retry, replyTo?.documentId) {
        if (replyTo != null) return@LaunchedEffect
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

    val goBack = { if (!sending) onBack() }
    BackHandler(onBack = goBack)
    MailPage(if (replyTo == null) "New message" else "Reply", goBack, action = {
        TextButton(enabled = (replyTo != null || selected != null) && body.isNotBlank() && !sending,
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White, disabledContentColor = Color.White.copy(alpha = 0.55f)),
            onClick = {
                if (requestId.isEmpty()) requestId = Uuid.random().toString()
                sending = true
                error = ""
                scope.launch {
                    try {
                        val count = if (kind == "reservation") {
                            MessageRepo.sendReservationMessage(replyTo?.reservationId ?: id, requestId, body, replyTo?.senderId)
                        } else {
                            MessageRepo.sendWaitlistNotice(site, id.takeIf { kind == "waitlist" }, requestId, body)
                        }
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
    }) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (replyTo != null) {
                Text("To " + replyTo.senderName.ifBlank { replyTo.senderId }, style = MaterialTheme.typography.titleMedium)
                Surface(color = Color.White, shape = MaterialTheme.shapes.small) {
                    Column(Modifier.fillMaxWidth().padding(14.dp)) {
                        Text(replyTo.title.ifBlank { "Reservation message" }, style = MaterialTheme.typography.titleSmall)
                        Text(replyTo.message, maxLines = 3, overflow = TextOverflow.Ellipsis)
                    }
                }
            } else {
                if (owner) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for ((value, label) in listOf("campsite" to "Campsite", "waitlist" to "Lottery", "reservation" to "Reservation")) {
                            FilterChip(kind == value, enabled = !sending, onClick = {
                                if (kind != value) {
                                    kind = value
                                    id = ""
                                    site = ""
                                    requestId = ""
                                    error = ""
                                }
                            }, label = { Text(label) })
                        }
                    }
                }
                when {
                    loadError != null -> {
                        Text(loadError.orEmpty(), color = MaterialTheme.colorScheme.error)
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
                        Box {
                            OutlinedButton(onClick = { menu = true }, enabled = !sending, modifier = Modifier.fillMaxWidth()) {
                                Text("To", style = MaterialTheme.typography.labelLarge)
                                Text(selected?.label ?: "Choose recipient", Modifier.weight(1f).padding(horizontal = 12.dp),
                                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                            DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                                choices.forEach { choice ->
                                    DropdownMenuItem(text = { Text(choice.label) }, onClick = {
                                        id = choice.id
                                        site = choice.site
                                        menu = false
                                        requestId = ""
                                        error = ""
                                    })
                                }
                            }
                        }
                        if (kind != "reservation") Text(
                            if (kind == "campsite") "Everyone on this campsite’s lottery waitlists" else "Everyone on this lottery’s waitlist",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            HorizontalDivider()
            OutlinedTextField(body, {
                if (it.length <= 2000) {
                    body = it
                    requestId = ""
                    error = ""
                }
            }, Modifier.fillMaxWidth(), enabled = !sending, label = { Text("Message") },
                minLines = 5, maxLines = 10,
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
            if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}
