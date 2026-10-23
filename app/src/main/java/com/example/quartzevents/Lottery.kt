package com.example.quartzevents

class Lottery(val campsite: Campsite, var registrationStart: String, var registrationEnd: String, var bookingStart: String, var bookingEnd: String, var capacity:Int?=null) {
    private class WaitingList{
        val entrants: MutableList<Entrant> = mutableListOf()
    }
    private var contestants = WaitingList()

}