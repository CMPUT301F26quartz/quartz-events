/**
 * Represents a single campsite in the system.
 *
 * Stores essential details like name and location.
 * Can also hold a list of reservations.
 *
 * @property id The unique Firestore document ID.
 * @property ownerId The UID of the owner who created it.
 * @property name The name of the campsite.
 * @property location The city or address.
 * @property description Details and amenities of the site.
 * @property maxOccupancy The maximum number of guests allowed.
 * @property imageUrl A link to the campsite's photo.
 * @property tags Search keywords like Forest or Lake.
 * @property reservations A list of booked reservations.
 */

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

