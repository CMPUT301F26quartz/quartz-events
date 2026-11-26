package com.example.quartzevents.Messaging

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.Alignment
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quartzevents.Repositories.MessageRepo
import com.example.quartzevents.model.UserProfile
import android.icu.text.DateFormat

@Composable
fun MessageCenter(profile: UserProfile, onBack: () -> Unit, onAddCampsite: () -> Unit) {
    var sent by rememberSaveable(profile.uid) { mutableStateOf(false) }
    var notificationsOnly by rememberSaveable(profile.uid) { mutableStateOf(false) }
    var composing by rememberSaveable(profile.uid) { mutableStateOf(false) }
    var replyId by rememberSaveable(profile.uid) { mutableStateOf<String?>(null) }
    var receipt by rememberSaveable(profile.uid) { mutableStateOf("") }
    if (composing) {
        ComposeMail(profile.uid, profile.role == "owner", replyId, onAddCampsite, onBack = { composing = false }, onSent = { count ->
            composing = false
            sent = true
            notificationsOnly = false
            receipt = "Sent to " + count + if (count == 1) " recipient." else " recipients."
        })
        return
    }
    var retry by remember { mutableIntStateOf(0) }
    var expanded by rememberSaveable(profile.uid) { mutableStateOf<String?>(null) }
    val repo = remember(profile.uid, sent, retry) { MessageRepo.mailboxFor(profile.uid, sent) }
    DisposableEffect(repo) { onDispose { repo.stopListening() } }
    val ownerSent = sent && profile.role == "owner"
    val showNotificationsOnly = ownerSent && notificationsOnly
    val messages = if (showNotificationsOnly) repo.getAllNotifications() else repo.getAllMessages()
    val notices = messages.sortedByDescending { it.sentAt }
    // A broadcast is stored once per recipient. Show one card for it in Sent.
    val groups = notices.groupBy {
        if (sent && it.broadcastId.isNotBlank()) "broadcast/" + it.broadcastId else "message/" + it.documentId
    }
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("Back") }
            Text("Messages", Modifier.weight(1f), style = MaterialTheme.typography.headlineMedium)
            IconButton(onClick = { replyId = null; composing = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Compose new message")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(!sent, onClick = { sent = false; receipt = "" }, label = { Text("Inbox") })
            FilterChip(sent, onClick = { sent = true }, label = { Text("Sent") })
        }
        if (ownerSent) {
            FilterChip(notificationsOnly, onClick = { notificationsOnly = !notificationsOnly },
                label = { Text("Notifications only") })
        }
        if (receipt.isNotBlank()) Text(receipt)
        Spacer(Modifier.height(8.dp))
        when {
            repo.isLoading -> CircularProgressIndicator()
            repo.errorMessage != null -> {
                Text("Could not load messages.")
                TextButton(onClick = { retry++ }) { Text("Retry") }
            }
            groups.isEmpty() -> Text(if (showNotificationsOnly) "No sent notifications yet." else "No messages yet.")
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(groups.entries.toList(), key = { it.key }) { (key, deliveries) ->
                    val notice = deliveries.first()
                    Card(onClick = { expanded = if (expanded == key) null else key }) {
                        Column(Modifier.fillMaxWidth().padding(14.dp)) {
                            Text(notice.title.ifBlank { "Notification" }, fontWeight = FontWeight.Bold)
                            if (sent) Text("Recipients: " + deliveries.size)
                            else Text("From: " + notice.senderName.ifBlank { notice.senderId })
                            notice.sentAt?.let { Text(DateFormat.getDateTimeInstance().format(it.toDate())) }
                            Text(notice.message, maxLines = if (expanded == key) Int.MAX_VALUE else 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            if (sent && expanded == key) Text(deliveries.joinToString { it.receiverName.ifBlank { it.receiverId } })
                            if (!sent && expanded == key && notice.type == "direct" && notice.reservationId.isNotBlank()) {
                                TextButton(onClick = { replyId = notice.reservationId; composing = true }) { Text("Reply") }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Keeps the mailbox reachable on an empty account and surfaces new draw results on the home page. */
@Composable
internal fun HomeMessages(uid: String, onOpen: () -> Unit) {
    val repo = remember(uid) { MessageRepo.mailboxFor(uid, false) }
    DisposableEffect(repo) { onDispose { repo.stopListening() } }
    val result = repo.getAllNotifications().filter {
        it.kind in setOf("draw_complete", "lottery_won", "lottery_lost")
    }.maxByOrNull { it.sentAt?.seconds ?: 0L }
    result?.let { Text(it.title, style = MaterialTheme.typography.titleMedium) }
    OutlinedButton(onClick = onOpen) { Text(if (result == null) "Messages" else "View messages") }
}
