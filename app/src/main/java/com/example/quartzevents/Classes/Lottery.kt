package com.example.quartzevents.Classes

import com.google.firebase.Timestamp


/**
 *
 */
class Lottery(
    var campsites: MutableList<String> = mutableListOf(),
    val name: String ="",
    var registrationStart: String = "",
    var registrationEnd: String = "",
    var bookingStart: String = "",
    var bookingEnd: String = "",
    var capacity: Int? =null,
    val id: String? =null,
    val creatorId: String? =null,
    var entrants: MutableList<String> = mutableListOf(),
    var winNum: Int? =null,
    var winners: MutableList<String> = mutableListOf(),
    val drawnAt: Timestamp? = null
) {


    @com.google.firebase.firestore.Exclude
    fun isWaitlistFull(): Boolean = capacity?.let { it > 0 && entrants.size >= it } == true

    fun add_contestant(contestant: Entrant) {
        entrants.add(contestant.uid)
    }

    fun remove_contestant(contestant: Entrant) {
        entrants.remove(contestant.uid)
    }

    fun clear_contestant_list() {
        entrants.clear()
    }


    fun getWinNotif(winnerId: String, ownerId: String): Notification {
        return Notification(ownerId,winnerId,"Congrats! You won the $name lottery!")
    }

    fun getLoseNotif(loserId: String, ownerId: String): Notification {
        return Notification(ownerId,loserId,"Unfortunately, you did not win the $name lottery. Better Luck next time.")
    }
}
