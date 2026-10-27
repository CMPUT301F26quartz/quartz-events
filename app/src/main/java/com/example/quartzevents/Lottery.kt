package com.example.quartzevents

import kotlin.to

class Lottery(val campsites: MutableMap<Campsite, LotteryInfo>) {

    val entrants: MutableList<Entrant> = mutableListOf()

    fun add_contestant(contestant: Entrant){
        entrants.add(contestant)
    }

    fun remove_contestant(contestant: Entrant){
        entrants.remove(contestant)
    }

    fun clear_contestant_list(){
        entrants.clear()
    }

    fun select_winner(){

    }

}