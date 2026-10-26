package com.example.quartzevents

class Lottery(val campsite: Campsite, var registrationStart: String, var registrationEnd: String, var bookingStart: String, var bookingEnd: String, var capacity:Int?=null) {
    private class WaitingList{ // maybe not needed, could just make a private var mutable list of entrants in Lottery class which would accomplish the same thing
        val entrants: MutableList<Entrant> = mutableListOf()
    }
    private var contestants = WaitingList()

    fun add_contestant(contestant: Entrant){
        contestants.entrants.add(contestant)
    }

    fun remove_contestant(contestant: Entrant){
        contestants.entrants.remove(contestant)
    }

    fun clear_contestant_list(){
        contestants.entrants.clear()
    }

    fun select_winner(){

    }

}