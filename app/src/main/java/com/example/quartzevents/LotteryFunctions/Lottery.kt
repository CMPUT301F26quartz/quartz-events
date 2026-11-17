package com.example.quartzevents.LotteryFunctions

import com.example.quartzevents.Campsite
import com.example.quartzevents.Date
import com.example.quartzevents.Entrant

class Lottery(
    val campsites: MutableList<Campsite> = mutableListOf(),
    val name: String ="",
    var registrationStart: Date = Date(""),
    var registrationEnd: Date = Date(""),
    var bookingStart: Date = Date(""),
    var bookingEnd: Date = Date(""),
    var capacity:Int?=null,
    val id: String?=null,
    val entrants: MutableList<Entrant> = mutableListOf(),
    var winNum: Int?=null,
    val winners: MutableList<Entrant> = mutableListOf()
) {


    fun add_contestant(contestant: Entrant) {
        entrants.add(contestant)
    }

    fun remove_contestant(contestant: Entrant) {
        entrants.remove(contestant)
    }

    fun clear_contestant_list() {
        entrants.clear()
    }
}