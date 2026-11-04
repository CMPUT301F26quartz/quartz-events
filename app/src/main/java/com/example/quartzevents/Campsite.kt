package com.example.quartzevents

import com.example.quartzevents.Reservation

class Campsite() {
    val description: String = ""
    val id: String = ""
    val imageUrl: String = ""
    val location: String = ""
    val maxOccupancy: String = ""
    val name: String = ""
    val ownerId: String = ""
    val tags: String = ""
    val reservations: MutableList<Reservation> = mutableListOf()

    fun addReservation(new: Reservation){
        reservations.add(new)
    }
}