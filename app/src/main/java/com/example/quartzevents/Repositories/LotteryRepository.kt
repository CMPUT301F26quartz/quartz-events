package com.example.quartzevents.Repositories

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.quartzevents.Classes.Entrant
import com.example.quartzevents.Classes.Lottery
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import android.text.format.DateFormat
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue

/**
 * Repository class for Lottery objects and their associated collection in the database
 *
 * Connects to the database to create and maintain a list of Lottery objects from the Lotteries collection.
 */
class LotteryRepository {

    companion object {
        internal fun today() = DateFormat.format("yyyy-MM-dd", System.currentTimeMillis()).toString()

        /** Existing documents use IDs, document references or embedded records for links. */
        internal fun readLottery(doc: DocumentSnapshot): Lottery {
            fun ids(field: String, collection: String, key: String) =
                (doc.get(field) as? List<*>).orEmpty().mapNotNull {
                    MessageRepo.memberId(it, collection, key)
                }.filter { it.isNotBlank() }.distinct().toMutableList()
            fun date(field: String): String {
                val raw = doc.get(field)
                val value = (if (raw is Map<*, *>) raw["date"] else raw) as? String ?: return ""
                val parts = value.split("-")
                return if (parts.size == 3) listOf(parts[0], parts[1].padStart(2, '0'),
                    parts[2].padStart(2, '0')).joinToString("-") else value
            }
            return Lottery(id = doc.id, name = doc.getString("name") ?: doc.id,
                campsites = ids("campsites", "campsites", "id"), entrants = ids("entrants", "users", "uid"),
                winners = ids("winners", "users", "uid"), creatorId = doc.getString("creatorId"),
                registrationStart = date("registrationStart"), registrationEnd = date("registrationEnd"),
                bookingStart = date("bookingStart"), bookingEnd = date("bookingEnd"),
                capacity = doc.getLong("capacity")?.toInt(), winNum = doc.getLong("winNum")?.toInt(),
                drawnAt = doc.getTimestamp("drawnAt"))
        }

        internal fun setMembership(id: String, uid: String, join: Boolean): Task<Unit> {
            val db = Firebase.firestore
            val ref = db.collection("Lotteries").document(id)
            return db.runTransaction { tx ->
                val doc = tx.get(ref)
                check(doc.exists()) { "This lottery is no longer available." }
                val lottery = readLottery(doc)
                check(lottery.registrationOpen()) {
                    "Registration is closed."
                }
                if (join && uid !in lottery.entrants) {
                    check(!lottery.isWaitlistFull()) {
                        "This waitlist is full."
                    }
                    tx.update(ref, "entrants", FieldValue.arrayUnion(uid))
                } else if (!join && uid in lottery.entrants) {
                    val entries = doc.get("entrants") as List<*>
                    tx.update(ref, "entrants", entries.filterNot { MessageRepo.memberId(it, "users", "uid") == uid })
                }
                Unit
            }
        }
    }

    private val db = Firebase.firestore

    private val lotteryRef = db.collection("Lotteries")
    private val _lotteries = mutableStateListOf<Lottery>()

    /** Whether the initial Lottery list is still loading. */
    var isLoading by mutableStateOf(true)
        private set

    /** The most recent list-loading error, or null when the listener succeeds. */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val listener: ListenerRegistration = lotteryRef.addSnapshotListener { snapshot, error ->
        isLoading = false
        if (error != null) {
            errorMessage = "Couldn't load lotteries. Please retry."
            return@addSnapshotListener
        }

        errorMessage = null
        _lotteries.clear()

        snapshot?.documents?.forEach { document ->
            _lotteries.add(readLottery(document))
        }
        _lotteries.sortBy { it.name.lowercase() }
    }

    /** Removes the live list listener when its owning page leaves the composition. */
    fun stopListening() {
        listener.remove()
    }

    /**
     * A function that returns a mutable list of Lottery Objects built from all documents present
     * in the Lotteries collection in the firebase database.
     *
     * On successful execution, the function returns a mutable list of Lottery objects.
     * On unsuccessful execution, the function throws an exception.
     *
     * @property onResult a lambda function that creates a mutable list of Lottery objects on success, and
     * an exception message on failure
     */
    fun getAllLotteries(): List<Lottery> {
        return _lotteries
    }

    /** Returns a specific lottery using the lottery id */
    fun getLottery(id: String): Lottery? {
        for (lottery in _lotteries) {
            if (lottery.id == id) return lottery
        }
        errorMessage = "Could not fine campsite."
        return null
    }


    /**
     * Creates a new document in the Lotteries collection from a lottery object.
     *
     * @property lottery a Lottery Object to be added to database
     */
    fun addLottery(lottery: Lottery) {
        val lotteryId = lotteryRef.document().id
        saveLottery(lotteryId, lottery)
    }

    /**
     * Removes a document from the Lotteries collection from the unique id of the lottery to be removed.
     *
     * @property lottery a Lottery object to be removed from the database
     */
    fun deleteLottery(lottery: Lottery) {
        lotteryRef.document(lottery.id.toString()).delete()
            .addOnSuccessListener { Log.d("LotteryDeletion","Lottery Deleted Successfully") }
            .addOnFailureListener { error -> Log.d("LotteryDeletion","Failure to Delete Lottery", error) }
    }

    /**
     * Replaces the stored lottery with its updated information.
     *
     * @property lottery a Lottery object to be used to update the document
     */
    fun updateLottery(fields: MutableMap<String,Any>,lotteryId: String){
        lotteryRef.document(lotteryId).update(fields)
            .addOnSuccessListener { Log.d("LotteryUpdate","Lottery Updated Successfully") }
            .addOnFailureListener { error -> Log.d("LotteryUpdate","Failure to Update Lottery",error) }

    }

    private fun saveLottery(id: String, lottery: Lottery) {
        lotteryRef.document(id).set(lottery)
            .addOnSuccessListener { Log.d("LotteryCreation","Lottery Created Successfully") }
            .addOnFailureListener { error -> Log.d("LotteryCreation","Failure to Create Lottery", error) }
        lotteryRef.document(id).update("id",id)
            .addOnSuccessListener { Log.d("LotteryCreation","Lottery Id Inserted Successfully") }
            .addOnFailureListener { error -> Log.d("LotteryCreation","Failure to insert Lottery Id", error) }
    }

    fun addEntrant(lottery: Lottery, entrant: Entrant) =
        setMembership(requireNotNull(lottery.id), entrant.uid, true)

    fun deleteEntrant(lottery: Lottery, entrant: Entrant) =
        setMembership(requireNotNull(lottery.id), entrant.uid, false)

    /** Commit one result and its inbox audience together, including for an empty waitlist. */
    fun selectWinner(ownerId: String, lottery: Lottery): Task<Unit> {
        val ref = lotteryRef.document(requireNotNull(lottery.id))
        return db.runTransaction { tx ->
            val doc = tx.get(ref)
            if (!doc.exists()) return@runTransaction
            val draw = readLottery(doc)
            if (draw.creatorId != ownerId || draw.drawnAt != null ||
                !com.example.quartzevents.Classes.Date(draw.registrationEnd).isValidDate() ||
                draw.registrationEnd >= today()) return@runTransaction
            val winners = draw.winners.ifEmpty {
                draw.entrants.shuffled().take((draw.winNum ?: 1).coerceAtLeast(0))
            }
            tx.update(ref, mapOf("winners" to winners, "drawnAt" to FieldValue.serverTimestamp(),
                "resultRecipients" to (draw.entrants + winners + ownerId).distinct()))
            Unit
        }
    }
}
