package com.example.quartzevents

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class LotteryRepository {

    private val _lotteries: MutableList<Lottery> = mutableStateListOf()
    val db = Firebase.firestore

    val lotteryRef =db.collection("Lotteries")

    val lotteries: List<Lottery>
        get() = _lotteries


    fun addLottery(lottery: Lottery) {
        val lotteryId = lotteryRef.document().id
        lotteryRef.document(lotteryId).set(lottery)
    }

    fun deleteLottery(lottery: Lottery) {
        lotteryRef.document(lottery.id.toString()).delete()
    }

    fun addEntrant(lottery: Lottery, entrant: Entrant) {
        val ents = lottery.entrants
        ents.add(entrant)

        deleteLottery(lottery)
        lotteryRef.document(lottery.id.toString()).set(Lottery(lottery.campsites,lottery.name,
            lottery.registrationStart,lottery.registrationEnd,lottery.bookingStart,lottery.bookingEnd,lottery.capacity,lottery.id,ents, lottery.winNum))
    }

    fun deleteEntrant(lottery: Lottery, entrant: Entrant) {
        val ents = lottery.entrants
        ents.remove(entrant)

        deleteLottery(lottery)
        lotteryRef.document(lottery.id.toString()).set(Lottery(lottery.campsites,lottery.name,
            lottery.registrationStart,lottery.registrationEnd,lottery.bookingStart,lottery.bookingEnd,lottery.capacity,lottery.id,ents, lottery.winNum))
    }

    init{
        lotteryRef.addSnapshotListener { snapshot, error ->
            if (error != null){
                return@addSnapshotListener
            }

            _lotteries.clear()

            snapshot?.documents?.forEach { document ->
                val lottery = document.toObject(Lottery::class.java)
                if (lottery != null){
                    _lotteries.add(lottery)
                }
            }

        }
    }
}