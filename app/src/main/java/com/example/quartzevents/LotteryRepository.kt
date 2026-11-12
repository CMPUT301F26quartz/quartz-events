/**
 * Repository class for Lottery objects and their associated collection in the database
 *
 * Connects to the database to create and maintain a list of Lottery objects from the Lotteries collection.
 */

package com.example.quartzevents

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class LotteryRepository {

    private val db = FirebaseFirestore.getInstance()

    private val lotteryRef = db.collection("Lotteries")

    var lotteries: MutableList<Lottery> = mutableListOf()

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
    fun getAllLotteries(
        onResult: (List<Lottery>?, String?) -> Unit
    ) {
        db.collection("campsites")
            .get()
            .addOnSuccessListener { result ->
                lotteries = result.toObjects(Lottery::class.java)
                onResult(lotteries, null)
            }
            .addOnFailureListener { exception ->
                onResult(null, exception.message)
            }
    }


    /**
     * Creates a new document in the Lotteries collection from a lottery object.
     *
     * @property lottery a Lottery Object to be added to database
     */
    fun addLottery(lottery: Lottery) {
        val lotteryId = lotteryRef.document().id
        lotteryRef.document(lotteryId).set(lottery)
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
    fun updateLottery(lottery: Lottery){
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

        updateLottery(Lottery(lottery.campsites,lottery.name,
            lottery.registrationStart,lottery.registrationEnd,lottery.bookingStart,lottery.bookingEnd,lottery.capacity,lottery.id,ents, lottery.winNum))
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

        updateLottery(Lottery(lottery.campsites,lottery.name,
            lottery.registrationStart,lottery.registrationEnd,lottery.bookingStart,lottery.bookingEnd,lottery.capacity,lottery.id,ents, lottery.winNum))
    }


    /**
     * init function that listens for updates to the database collection in order to update lotteries list with
     * new entries.
     */
    init{
        lotteryRef.addSnapshotListener { snapshot, error ->
            if (error != null){
                return@addSnapshotListener
            }

            lotteries.clear()

            snapshot?.documents?.forEach { document ->
                val lottery = document.toObject(Lottery::class.java)
                if (lottery != null){
                    lotteries.add(lottery)
                }
            }

        }
    }
}