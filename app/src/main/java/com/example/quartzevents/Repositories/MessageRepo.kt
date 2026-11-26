package com.example.quartzevents.Repositories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.quartzevents.Classes.Notification
import com.example.quartzevents.Classes.DirectMessage
import com.example.quartzevents.Classes.Message
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

internal data class RecipientOption(val id: String, val site: String, val label: String)

class MessageRepo private constructor(messagesRef: Query) {
    constructor() : this(Firebase.firestore.collection("messages"))

    companion object {
        /** Subscribe only to this user's sent or received messages. */
        fun mailboxFor(uid: String, sent: Boolean): MessageRepo {
            val field = if (sent) "senderId" else "receiverId"
            return MessageRepo(Firebase.firestore.collection("messages")
                .whereEqualTo(field, uid))
        }

        /** Reads only the open compose tab, using the signed-in user's scoped queries. */
        internal suspend fun recipientOptions(uid: String, owner: Boolean, kind: String): List<RecipientOption> {
            val db = Firebase.firestore
            val bookings = db.collection("Reservations")
            val sites = db.collection("campsites")
            val options = mutableListOf<RecipientOption>()
            if (!owner) {
                require(kind == "reservation")
                val mine = bookings.whereEqualTo("entrantId", uid).get(Source.SERVER).await()
                val bySite = mine.documents.groupBy { it.getString("campsiteId") }
                for ((siteId, stays) in bySite) {
                    if (siteId.isNullOrBlank() || '/' in siteId) continue
                    val site = sites.document(siteId).get(Source.SERVER).await()
                    val contact = site.getString("ownerId")?.takeIf { it.isNotBlank() && it != uid } ?: continue
                    for (stay in stays) options += reservationOption(stay, site, contact)
                }
            } else {
                val mine = sites.whereEqualTo("ownerId", uid).get(Source.SERVER).await()
                for (site in mine.documents) {
                    val name = site.getString("name").orEmpty().ifBlank { site.id }
                    when (kind) {
                        "campsite" -> options += RecipientOption(site.id, site.id, name)
                        "waitlist" -> {
                            val draws = lotteriesFor(site)
                            for (draw in draws) options += RecipientOption(draw.id, site.id,
                                "$name / ${draw.getString("name") ?: draw.id}")
                        }
                        "reservation" -> {
                            val stays = bookings.whereEqualTo("campsiteId", site.id).get(Source.SERVER).await()
                            for (stay in stays) {
                                val entrant = stay.getString("entrantId")?.takeIf { it.isNotBlank() && it != uid } ?: continue
                                options += reservationOption(stay, site, stay.getString("entrantName").orEmpty().ifBlank { entrant })
                            }
                        }
                    }
                }
            }
            return options.sortedBy { it.label.lowercase() }
        }

        private fun reservationOption(stay: DocumentSnapshot, site: DocumentSnapshot, contact: String) =
            RecipientOption(stay.id, "", listOfNotNull(contact, site.getString("name") ?: site.id,
                stay.getString("startDate"), stay.id).joinToString(" · "))

        internal suspend fun lotteriesFor(site: DocumentSnapshot): List<DocumentSnapshot> {
            val links = listOfNotNull(site.reference, site.id, mapOf("id" to site.id),
                site.data?.takeIf { it["id"] == site.id }).distinct()
            val draws = Firebase.firestore.collection("Lotteries")
            val linked = draws.whereArrayContainsAny("campsites", links).get(Source.SERVER).await()
            val indexed = draws.whereArrayContains("messagingCampsiteIds", site.id).get(Source.SERVER).await()
            return (linked.documents + indexed.documents).distinctBy { it.id }.filter { siteIndex(it, site.id) >= 0 }
        }

        private fun siteIndex(draw: DocumentSnapshot, campsiteId: String): Int =
            (draw.get("campsites") as? List<*>)?.indexOfFirst { memberId(it, "campsites", "id") == campsiteId } ?: -1

        private fun memberId(value: Any?, collection: String, key: String): String? = when (value) {
            is String -> value
            is DocumentReference -> value.id.takeIf { value.parent.path == collection }
            is Map<*, *> -> value[key] as? String
            else -> null
        }

        private suspend fun sendOnce(requestId: String, text: String, target: String,
                                     write: (Transaction, String, String) -> Int): Int {
            val uid = FirebaseAuth.getInstance().uid ?: error("Sign in to send messages.")
            val body = text.trim()
            require(body.isNotEmpty() && body.length <= 2000) { "Enter a message of up to 2000 characters." }
            val db = Firebase.firestore
            val receipt = db.collection("users").document(uid).collection("messageRequests").document(requestId)
            return db.runTransaction { tx ->
                val prior = tx.get(receipt)
                if (prior.exists()) {
                    check(prior.getString("target") == target && prior.getString("body") == body) {
                        "This request was already sent. Compose a new message."
                    }
                    return@runTransaction prior.getLong("count")!!.toInt()
                }
                val count = write(tx, uid, body)
                tx.set(receipt, mapOf("target" to target, "body" to body, "count" to count))
                count
            }.await()
        }

        internal suspend fun sendReservationMessage(id: String, requestId: String, body: String): Int =
            sendOnce(requestId, body, "Reservations/$id") { tx, uid, text ->
                val db = Firebase.firestore
                val booking = tx.get(db.collection("Reservations").document(id))
                val siteId = booking.getString("campsiteId") ?: error("This reservation no longer has a campsite.")
                val site = tx.get(db.collection("campsites").document(siteId))
                val owner = site.getString("ownerId")
                val entrant = booking.getString("entrantId")
                val receiver = when (uid) {
                    owner -> entrant
                    entrant -> owner
                    else -> error("This reservation does not belong to your account.")
                }
                check(!receiver.isNullOrBlank() && receiver != uid) { "No linked reservation contact." }
                val sender = tx.get(db.collection("users").document(uid)).getString("name") ?: uid
                tx.set(db.collection("messages").document(), mapOf(
                    "type" to "direct", "senderId" to uid, "senderName" to sender, "receiverId" to receiver,
                    "reservationId" to id, "title" to (site.getString("name") ?: siteId),
                    "message" to text, "sentAt" to FieldValue.serverTimestamp()))
                1
            }

        internal suspend fun sendWaitlistNotice(campsiteId: String, lotteryId: String?, requestId: String, body: String): Int {
            val db = Firebase.firestore
            val siteRef = db.collection("campsites").document(campsiteId)
            val drawRefs = if (lotteryId != null) listOf(db.collection("Lotteries").document(lotteryId))
                else {
                    lotteriesFor(siteRef.get(Source.SERVER).await()).map { it.reference }
                }
            return sendOnce(requestId, body, "campsites/$campsiteId/" + lotteryId.orEmpty()) { tx, uid, text ->
                val site = tx.get(siteRef)
                check(site.getString("ownerId") == uid) { "Choose a campsite you own." }
                val sender = tx.get(db.collection("users").document(uid)).getString("name") ?: uid
                val seen = mutableSetOf<String>()
                // Read every selected draw before starting any writes in this transaction.
                val draws = drawRefs.map { tx.get(it) }.mapNotNull { draw ->
                    siteIndex(draw, campsiteId).takeIf { it >= 0 }?.let { draw to it }
                }
                check(draws.isNotEmpty()) {
                    if (lotteryId == null) "No linked lottery was found for this campsite."
                    else "This lottery is no longer linked to this campsite."
                }
                for ((draw, siteIndex) in draws) {
                    val entrants = draw.get("entrants") as? List<*> ?: continue
                    for ((index, member) in entrants.withIndex()) {
                        val receiver = memberId(member, "users", "uid")
                        check(!receiver.isNullOrBlank() && '/' !in receiver) { "A waitlist entry has no linked user." }
                        if (!seen.add(receiver)) continue
                        val title = if (lotteryId != null) draw.getString("name") else site.getString("name")
                        tx.set(db.collection("messages").document(), mapOf(
                            "type" to "notification", "kind" to "waitlist_notice", "senderId" to uid, "senderName" to sender,
                            "receiverId" to receiver, "broadcastId" to "${uid}_$requestId", "title" to (title ?: "Campsite notice"),
                            "campsiteId" to campsiteId, "lotteryId" to draw.id, "siteIndex" to siteIndex, "entrantIndex" to index,
                            "message" to text, "sentAt" to FieldValue.serverTimestamp()))
                    }
                }
                check(seen.isNotEmpty()) {
                    if (lotteryId == null) "No entrants have joined this campsite’s lottery waitlists yet."
                    else "No entrants have joined this lottery’s waitlist yet."
                }
                seen.size
            }
        }

        internal fun sendError(error: Exception): String = when ((error as? FirebaseFirestoreException)?.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Firestore denied this send. Check your account and the messaging rules."
            FirebaseFirestoreException.Code.UNAVAILABLE -> "Firestore is unavailable. Check your connection and retry."
            else -> error.localizedMessage ?: "Could not send. Please retry."
        }
    }
    private val _messages = mutableStateListOf<Message>()

    var isLoading by mutableStateOf(true)
        private set
    /** The most recent list-loading error, or null when the listener succeeds. */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val listener: ListenerRegistration = messagesRef.addSnapshotListener { snapshot, error ->
        isLoading = false
        _messages.clear()
        if (error != null) {
            errorMessage = "Couldn't load messages. Please retry."
            return@addSnapshotListener
        }

        errorMessage = null
        snapshot?.documents?.forEach { document ->
            val message: Message? = when (document.getString("type")) {
                "notification"   -> document.toObject(Notification::class.java)
                "direct"   -> document.toObject(DirectMessage::class.java)
                else      -> null
            }
            if (message != null) _messages.add(message)
        }
    }

    /** Removes the live list listener when its owning page leaves the composition. */
    fun stopListening() {
        listener.remove()
    }

    fun getAllMessages():List<Message>{
        return _messages
    }

    fun getAllNotifications():List<Notification>{
        return _messages.filterIsInstance<Notification>()
    }

    fun getAllDirectMessages():List<DirectMessage>{
        return _messages.filterIsInstance<DirectMessage>()
    }

    fun getAllMessagesBetween(user1: String, user2: String): List<DirectMessage>{
        return getAllDirectMessages().filter {
            (it.senderId == user1 && it.receiverId == user2) ||
                (it.senderId == user2 && it.receiverId == user1)
        }
    }
}
