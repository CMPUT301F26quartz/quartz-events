package com.example.quartzevents

class Entrant(
    email: String = "",
    name: String = "",
    uid: String = ""
) : User(email, name, role = "entrant", uid = uid) {

    val reservations: MutableList<Reservation> = mutableListOf()
    var notificationsOn = true
    val messages: MutableList<Message> = mutableListOf()
}