
package com.example.quartzevents.Repositories

import com.example.quartzevents.Classes.Lottery
import com.example.quartzevents.Classes.Reservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import kotlin.text.get

class ReservationRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val reservations = db.collection("Reservations")
    private val lotteries = db.collection("Lotteries")
    private val bookingLocks = db.collection("CampsiteBookingLocks")

    // Get lotteries won by the logged-in entrant.
    fun getWonLotteries(
        onResult: (List<Pair<String, Lottery>>?, String?) -> Unit
    ) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            onResult(null, "Please log in first.")
            return
        }

        lotteries.get()
            .addOnSuccessListener { snapshot ->
                try {
                    val wonLotteries = snapshot.documents.mapNotNull { document ->
                        val lottery = document.toObject(Lottery::class.java)

                        if (lottery != null &&
                            lottery.winners.any { it.uid == uid }
                        ) {
                            Pair(document.id, lottery)
                        } else {
                            null
                        }
                    }

                    onResult(wonLotteries, null)
                } catch (e: Exception) {
                    onResult(
                        null,
                        e.message ?: "Could not load lotteries."
                    )
                }
            }
            .addOnFailureListener { error ->
                onResult(
                    null,
                    error.message ?: "Could not load lotteries."
                )
            }
    }

    // Return the nights occupied by a reservation.
    // Checkout day is not included.
    private fun getBookedNights(
        start: LocalDate,
        end: LocalDate
    ): List<String> {
        val nights = mutableListOf<String>()
        var current = start

        while (current.isBefore(end)) {
            nights.add(current.toString())
            current = current.plusDays(1)
        }

        return nights
    }

    // Create a reservation for a lottery winner.
    fun makeReservation(
        lotteryId: String,
        campsiteId: String,
        guests: Int,
        startDate: String,
        endDate: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            onResult(false, "Please log in first.")
            return
        }

        if (lotteryId.isBlank() || campsiteId.isBlank()) {
            onResult(false, "Please select a lottery and campsite.")
            return
        }

        val start: LocalDate
        val end: LocalDate

        try {
            start = LocalDate.parse(startDate)
            end = LocalDate.parse(endDate)
        } catch (e: Exception) {
            onResult(false, "Please use yyyy-MM-dd for dates.")
            return
        }

        if (start.isBefore(LocalDate.now())) {
            onResult(false, "Check-in cannot be in the past.")
            return
        }

        if (!start.isBefore(end)) {
            onResult(false, "Check-out must be after check-in.")
            return
        }

        if (guests < 1) {
            onResult(false, "At least one guest is required.")
            return
        }

        val lotteryRef = lotteries.document(lotteryId)
        val lockRef = bookingLocks.document(campsiteId)
        val reservationRef = reservations.document()

        db.runTransaction { transaction ->

            // All reads happen before writes.
            val lotterySnapshot = transaction.get(lotteryRef)
            val lockSnapshot = transaction.get(lockRef)

            val lottery = lotterySnapshot.toObject(Lottery::class.java)
                ?: throw IllegalStateException("Lottery not found.")

            val isWinner = lottery.winners.any { it.uid == uid }

            val campsite = lottery.campsites.find {
                it.id == campsiteId
            }

            if (!isWinner || campsite == null) {
                throw IllegalStateException(
                    "You are not eligible for this reservation."
                )
            }

            val maxGuests = campsite.maxOccupancy.toIntOrNull()

            if (maxGuests == null ||
                maxGuests < 1 ||
                guests > maxGuests
            ) {
                throw IllegalStateException(
                    "Guest count exceeds campsite capacity."
                )
            }

            val bookedDates = (
                    lockSnapshot.get("bookedDates") as? Map<*, *>
                    )?.entries?.associate { entry ->
                    entry.key.toString() to entry.value.toString()
                }?.toMutableMap() ?: mutableMapOf()

            val requestedNights = getBookedNights(start, end)

            if (requestedNights.any { bookedDates.containsKey(it) }) {
                throw IllegalStateException(
                    "This campsite is already booked for those dates."
                )
            }

            requestedNights.forEach { night ->
                bookedDates[night] = reservationRef.id
            }

            val reservation = Reservation(
                id = reservationRef.id,
                entrantId = uid,
                entrantName = auth.currentUser?.displayName ?: "",
                campsiteId = campsiteId,
                lotteryId = lotteryId,
                guests = guests,
                startDate = startDate,
                endDate = endDate,
                status = "confirmed",
                createdAt = System.currentTimeMillis()
            )

            transaction.set(
                lockRef,
                mapOf("bookedDates" to bookedDates)
            )

            transaction.set(reservationRef, reservation)

            true
        }.addOnSuccessListener {
            onResult(true, "Reservation confirmed!")
        }.addOnFailureListener { error ->
            onResult(
                false,
                error.message ?: "Reservation failed."
            )
        }
    }

    // Get all reservations belonging to the logged-in entrant.
    fun getMyReservations(
        uid2:String = "",
        onResult: (List<Reservation>?, String?) -> Unit
    ) {
        var uid = auth.currentUser?.uid.toString()

        if(uid2 != "") uid = uid2

        if (uid == null) {
            onResult(null, "Please log in first.")
            return
        }

        reservations.whereEqualTo("entrantId", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    val list = snapshot.toObjects(
                        Reservation::class.java
                    )

                    onResult(
                        list.sortedByDescending { it.startDate },
                        null
                    )
                } catch (e: Exception) {
                    onResult(
                        null,
                        e.message ?: "Could not read reservations."
                    )
                }
            }
            .addOnFailureListener { error ->
                onResult(
                    null,
                    error.message ?: "Could not load reservations."
                )
            }
    }

    // Cancel a reservation and release its booked nights.
    fun cancelReservation(
        reservationId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            onResult(false, "Please log in first.")
            return
        }

        if (reservationId.isBlank()) {
            onResult(false, "Invalid reservation.")
            return
        }

        val reservationRef = reservations.document(reservationId)

        reservationRef.get()
            .addOnSuccessListener { initialSnapshot ->

                val initialReservation = initialSnapshot.toObject(
                    Reservation::class.java
                )

                if (initialReservation == null ||
                    initialReservation.entrantId != uid
                ) {
                    onResult(false, "Reservation not found.")
                    return@addOnSuccessListener
                }

                val lockRef = bookingLocks.document(
                    initialReservation.campsiteId
                )

                db.runTransaction { transaction ->

                    val reservationSnapshot =
                        transaction.get(reservationRef)

                    val lockSnapshot = transaction.get(lockRef)

                    val reservation = reservationSnapshot.toObject(
                        Reservation::class.java
                    ) ?: throw IllegalStateException(
                        "Reservation not found."
                    )

                    if (reservation.entrantId != uid ||
                        reservation.campsiteId !=
                        initialReservation.campsiteId
                    ) {
                        throw IllegalStateException(
                            "Reservation does not belong to you."
                        )
                    }

                    if (reservation.status != "confirmed") {
                        throw IllegalStateException(
                            "Reservation is already cancelled."
                        )
                    }

                    val start = LocalDate.parse(
                        reservation.startDate
                    )
                    val end = LocalDate.parse(
                        reservation.endDate
                    )

                    if (start.isBefore(LocalDate.now())) {
                        throw IllegalStateException(
                            "Started or past reservations cannot be cancelled."
                        )
                    }

                    val bookedDates = (
                            lockSnapshot.get("bookedDates") as? Map<*, *>
                            )?.entries?.associate { entry ->
                            entry.key.toString() to entry.value.toString()
                        }?.toMutableMap() ?: mutableMapOf()

                    getBookedNights(start, end).forEach { night ->
                        if (bookedDates[night] == reservationId) {
                            bookedDates.remove(night)
                        }
                    }

                    transaction.set(
                        lockRef,
                        mapOf("bookedDates" to bookedDates)
                    )

                    transaction.update(
                        reservationRef,
                        "status",
                        "cancelled"
                    )

                    true
                }.addOnSuccessListener {
                    onResult(
                        true,
                        "Reservation cancelled successfully."
                    )
                }.addOnFailureListener { error ->
                    onResult(
                        false,
                        error.message ?: "Cancellation failed."
                    )
                }
            }
            .addOnFailureListener { error ->
                onResult(
                    false,
                    error.message ?: "Could not load reservation."
                )
            }
    }

    // Get all reservations belonging to a campsite
    fun getCampsiteReservations(
        campId:String?,
        onResult: (List<Reservation>?, String?) -> Unit
    ) {

        if (campId == null) {
            onResult(null, "Please provide a campsite.")
            return
        }

        reservations.whereEqualTo("campsiteId", campId)
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    val list = snapshot.toObjects(
                        Reservation::class.java
                    )

                    onResult(
                        list.sortedByDescending { it.startDate },
                        null
                    )
                } catch (e: Exception) {
                    onResult(
                        null,
                        e.message ?: "Could not read reservations."
                    )
                }
            }
            .addOnFailureListener { error ->
                onResult(
                    null,
                    error.message ?: "Could not load reservations."
                )
            }
    }


}