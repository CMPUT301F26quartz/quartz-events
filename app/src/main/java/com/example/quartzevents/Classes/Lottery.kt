package com.example.quartzevents.Classes

import com.example.quartzevents.Campsite
import kotlin.random.Random

class Lottery(
    val campsites: MutableList<Campsite> = mutableListOf(),
    val name: String ="",
    var registrationStart: Date = Date(""),
    var registrationEnd: Date = Date(""),
    var bookingStart: Date = Date(""),
    var bookingEnd: Date = Date(""),
    var capacity:Int?=null,
    val id: String?=null,
    val creatorId: String?=null,
    val entrants: MutableList<Entrant> = mutableListOf(),
    var winNum: Int?=null,
    val winners: MutableList<Entrant> = mutableListOf()
) {
    var messagingCampsiteIds: List<String> = emptyList()


    fun add_contestant(contestant: Entrant) {
        entrants.add(contestant)
    }

    fun remove_contestant(contestant: Entrant) {
        entrants.remove(contestant)
    }

    fun clear_contestant_list() {
        entrants.clear()
    }

    fun selectWinner(owner: Owner) {
        for (i in 1..winNum!!) {
            val num = Random.nextInt(0, entrants.size)
            winners.add(entrants[num])
        }
        winners.forEach{ item ->
            sendWinNotif(item,owner)
        }
    }

    private fun sendWinNotif(winner: Entrant, owner: Owner) {
        val notif = Notification(owner,winner,"")
    }
}
