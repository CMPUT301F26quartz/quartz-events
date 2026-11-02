package com.example.quartzevents

import com.example.quartzevents.Reservation

class Campsite(val name: String, val location: String, var capacity: Int) {
    val reservations: MutableList<Reservation> = mutableListOf()
}