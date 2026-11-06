package com.example.quartzevents

import com.example.quartzevents.Reservation

data class Campsite(
    var createdAt: Int = 0,
    var description: String = "",
    var id: String = "",
    var imageUrl: String = "",
    var location: String = "",
    var maxOccupancy: String = "",
    var name: String = "",
    var ownerId: String = "",
    var tags: String = "",
){
    var reservations: MutableList<Reservation> = mutableListOf()

    fun addReservation(new: Reservation){
        reservations.add(new)
    }
}