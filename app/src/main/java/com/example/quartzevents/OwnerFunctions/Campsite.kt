// Template for adding campsite
package com.example.quartzevents


data class Campsite(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val location: String = "",
    val description: String = "",
    val maxOccupancy: String = "",
    val imageUrl: String = "",
    val tags: String = "",
){
    var reservations: MutableList<Reservation> = mutableListOf()

    fun addReservation(new: Reservation){
        reservations.add(new)
    }
}

