package com.example.quartzevents.Repositories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.quartzevents.Campsite
import com.example.quartzevents.Classes.Entrant
import com.example.quartzevents.Classes.Lottery
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

/**
 * Repository class for Lottery objects and their associated collection in the database
 *
 * Connects to the database to create and maintain a list of Lottery objects from the Lotteries collection.
 */
class LotteryRepository {

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
            _lotteries.add(document.toObject(Lottery::class.java)!!)
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
        val fullLottery = Lottery(
            lottery.campsites,
            lottery.name,
            lottery.registrationStart,
            lottery.registrationEnd,
            lottery.bookingStart,
            lottery.bookingEnd,
            lottery.capacity,
            lotteryId,
            lottery.creatorId,
            lottery.entrants,
            lottery.winNum,
            lottery.winners
        )
        lotteryRef.document(lotteryId).set(fullLottery)
    }

    /**
     * Removes a document from the Lotteries collection from the unique id of the lottery to be removed.
     *
     * @property lottery a Lottery object to be removed from the database
     */
    fun deleteLottery(lottery: Lottery) {
        lotteryRef.document(lottery.id.toString()).delete()
    }

    /**
     * Updates a document in the Lottery object by first removing it, then adding
     * it back to the collection with the same id and updated info.
     *
     * @property lottery a Lottery object to be used to update the document
     */
    fun updateLottery(lottery: Lottery) {
        deleteLottery(lottery)
        lotteryRef.document(lottery.id.toString()).set(lottery)
    }

    /**
     * Calls updateLottery in order to insert a new entrant into the lottery's waiting list.
     *
     * @property lottery a lottery object to pull info from for use in updating the document
     * @property entrant the Entrant object to be added to the waiting list
     */
    fun addEntrant(lottery: Lottery, entrant: Entrant) {
        val ents = lottery.entrants
        ents.add(entrant)

        updateLottery(
            Lottery(
                lottery.campsites,
                lottery.name,
                lottery.registrationStart,
                lottery.registrationEnd,
                lottery.bookingStart,
                lottery.bookingEnd,
                lottery.capacity,
                lottery.id,
                lottery.creatorId,
                ents,
                lottery.winNum,
                lottery.winners
            )
        )
    }

    /**
     * Calls updateLottery in order to remove an existing entrant from the lottery's waiting list.
     *
     * @property lottery a lottery object to pull info from for use in updating the document
     * @property entrant the Entrant object to be removed from the waiting list
     */
    fun deleteEntrant(lottery: Lottery, entrant: Entrant) {
        val ents = lottery.entrants
        ents.remove(entrant)

        updateLottery(
            Lottery(
                lottery.campsites,
                lottery.name,
                lottery.registrationStart,
                lottery.registrationEnd,
                lottery.bookingStart,
                lottery.bookingEnd,
                lottery.capacity,
                lottery.id,
                lottery.creatorId,
                ents,
                lottery.winNum,
                lottery.winners
            )
        )
    }
}
