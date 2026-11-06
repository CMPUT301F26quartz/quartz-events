package com.example.quartzevents.model

/**
 * Represents a user's profile containing personal information
 * and the user's role in the campsite reservation system.
 */
data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "entrant",
    val username: String = "",
    val phoneNumber: String = ""
)
