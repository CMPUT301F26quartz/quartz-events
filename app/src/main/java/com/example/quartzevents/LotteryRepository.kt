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
        lotteryRef.document().set(lottery)
    }

    fun deleteLottery(lottery: Lottery) {
        lotteryRef.document(lottery.id).delete()
    }

    fun addEntrant(lottery: Lottery, entrant: Entrant) {
        val campsites = lottery.campsites
        val name = lottery.name
        val regS = lottery.registrationStart
        val regE = lottery.registrationEnd
        val bookS = lottery.bookingStart
        val bookE = lottery.bookingEnd
        val cap = lottery.capacity
        val id = lottery.id
        val ents = lottery.entrants
        ents.add(entrant)

        deleteLottery(lottery)
        addLottery(Lottery(campsites,name,regS,regE,bookS,bookE,cap,id,ents))
    }

    fun deleteEntrant(lottery: Lottery, entrant: Entrant) {
        val campsites = lottery.campsites
        val name = lottery.name
        val regS = lottery.registrationStart
        val regE = lottery.registrationEnd
        val bookS = lottery.bookingStart
        val bookE = lottery.bookingEnd
        val cap = lottery.capacity
        val id = lottery.id
        val ents = lottery.entrants
        ents.remove(entrant)

        deleteLottery(lottery)
        addLottery(Lottery(campsites,name,regS,regE,bookS,bookE,cap,id,ents))
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