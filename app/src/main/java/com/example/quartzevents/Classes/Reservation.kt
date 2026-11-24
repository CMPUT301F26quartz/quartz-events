package com.example.quartzevents.Classes

data class Reservation(
    val id: String = "",
    val entrantId: String = "",
    val entrantName: String = "",
    val campsiteId: String = "",
    val lotteryId: String = "",
    val guests: Int = 1,
    val startDate: String = "",
    val endDate: String= "",
    val status: String = "confirmed",
    val createdAt: Long = 0L
)