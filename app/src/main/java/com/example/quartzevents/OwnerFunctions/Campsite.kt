// Template for adding campsite
package com.example.quartzevents.OwnerFunctions


data class Campsite(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val location: String = "",
    val description: String = "",
    val maxOccupancy: String = "",
    val imageUrl: String = "",
    val tags: String = "",
    val createdAt: Long = 0L
)