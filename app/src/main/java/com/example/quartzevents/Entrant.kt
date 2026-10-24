package com.example.quartzevents

class Entrant(email: String, name: String, password: String): User(email, name, password) {
    override fun userType(){
        val type = "entrant"
    }
    val reservations: MutableList<Reservation> = mutableListOf()
    var notificationsOn = true
    val messages: MutableList<Message> = mutableListOf()
}