package com.example.quartzevents

class Entrant(): User() {

    val reservations: MutableList<Reservation> = mutableListOf()
    var notificationsOn = true
    val messages: MutableList<Message> = mutableListOf()
}