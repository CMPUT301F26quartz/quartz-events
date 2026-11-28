package com.example.quartzevents.Messaging

import android.icu.text.DateFormat
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.quartzevents.Repositories.MessageRepo
import com.example.quartzevents.model.UserProfile
import com.example.quartzevents.ui.theme.Beige1
import com.example.quartzevents.ui.theme.Green1

private val mailColors = lightColorScheme(primary = Green1, surface = Beige1,
    secondaryContainer = Green1, onSecondaryContainer = Color.White)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MailPage(title: String, onBack: () -> Unit, action: @Composable RowScope.() -> Unit,
                      content: @Composable ColumnScope.() -> Unit) {
    MaterialTheme(colorScheme = mailColors, typography = MaterialTheme.typography) {
        Surface(Modifier.fillMaxSize()) {
            Column {
                TopAppBar(title = { Text(title, fontWeight = FontWeight.SemiBold) }, navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }, actions = action, colors = TopAppBarDefaults.topAppBarColors(containerColor = Green1,
                    titleContentColor = Color.White, navigationIconContentColor = Color.White, actionIconContentColor = Color.White))
                Column(Modifier.align(Alignment.CenterHorizontally).widthIn(max = 680.dp).fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
                    .imePadding(), content = content)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageCenter(profile: UserProfile, onBack: () -> Unit, onAddCampsite: () -> Unit) {
    var sent by rememberSaveable(profile.uid) { mutableStateOf(false) }
    var notificationsOnly by rememberSaveable(profile.uid) { mutableStateOf(false) }
    var newMessage by rememberSaveable(profile.uid) { mutableStateOf(false) }
    var replyId by rememberSaveable(profile.uid) { mutableStateOf<String?>(null) }
    var receipt by rememberSaveable(profile.uid) { mutableStateOf("") }
    var retry by remember { mutableIntStateOf(0) }
    var expanded by rememberSaveable(profile.uid) { mutableStateOf<String?>(null) }
    val repo = remember(profile.uid, sent, retry) { MessageRepo.mailboxFor(profile.uid, sent) }
    DisposableEffect(repo) { onDispose { repo.stopListening() } }
    val reply = replyId?.let { id -> repo.getAllMessages().find { it.documentId == id } }
    val closeComposer = { newMessage = false; replyId = null }
    if (newMessage || reply != null) {
        ComposeMail(profile.uid, profile.role == "owner", reply, onAddCampsite, closeComposer) { count ->
            closeComposer()
            sent = true
            notificationsOnly = false
            receipt = if (count == 1) "Sent to 1 recipient." else "Sent to $count recipients."
        }
        return
    }
    val ownerSent = sent && profile.role == "owner"
    val messages = if (ownerSent && notificationsOnly) repo.getAllNotifications() else repo.getAllMessages()
    val groups = messages.sortedByDescending { it.sentAt }.groupBy {
        if (sent && it.broadcastId.isNotBlank()) "broadcast/" + it.broadcastId else "message/" + it.documentId
    }
    BackHandler(onBack = onBack)
    MailPage("Messages", onBack, action = {
        IconButton(onClick = { replyId = null; newMessage = true }) { Icon(Icons.Default.Edit, "Compose new message") }
    }) {
        PrimaryTabRow(selectedTabIndex = if (sent) 1 else 0) {
            Tab(!sent, onClick = { sent = false; receipt = "" }, text = { Text("Inbox") })
            Tab(sent, onClick = { sent = true }, text = { Text("Sent") })
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp)) {
            if (ownerSent) FilterChip(notificationsOnly, onClick = { notificationsOnly = !notificationsOnly },
                label = { Text("Notifications only") })
            if (receipt.isNotBlank()) Text(receipt, color = Green1)
            when {
                repo.isLoading -> CircularProgressIndicator()
                repo.errorMessage != null -> {
                    Text("Could not load messages.")
                    TextButton(onClick = { retry++ }) { Text("Retry") }
                }
                groups.isEmpty() -> Text(when {
                    ownerSent && notificationsOnly -> "No sent notifications yet."
                    sent -> "No sent messages yet."
                    else -> "Your inbox is empty."
                }, Modifier.padding(vertical = 24.dp))
                else -> LazyColumn {
                    items(groups.entries.toList(), key = { it.key }) { (key, deliveries) ->
                        val notice = deliveries.first()
                        val open = expanded == key
                        Column(Modifier.fillMaxWidth().clickable { expanded = if (open) null else key }
                            .padding(vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val contact = when {
                                    !sent -> notice.senderName.ifBlank { notice.senderId }
                                    deliveries.size == 1 -> "To " + notice.receiverName.ifBlank { notice.receiverId }
                                    else -> "To ${deliveries.size} recipients"
                                }
                                Text(contact, Modifier.weight(1f), fontWeight = FontWeight.SemiBold,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                                notice.sentAt?.let {
                                    Text(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(it.toDate()),
                                        Modifier.padding(start = 8.dp), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Text(notice.title.ifBlank { if (notice.type == "direct") "Reservation message" else "Notification" },
                                color = Green1, style = MaterialTheme.typography.titleSmall)
                            Text(notice.message, maxLines = if (open) Int.MAX_VALUE else 2, overflow = TextOverflow.Ellipsis)
                            if (open && sent && deliveries.size > 1) Text(deliveries.joinToString { it.receiverName.ifBlank { it.receiverId } })
                            if (open && !sent && notice.type == "direct" && notice.reservationId.isNotBlank()) {
                                TextButton(onClick = { replyId = notice.documentId }) { Text("Reply") }
                            }
                        }
                        HorizontalDivider()
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
