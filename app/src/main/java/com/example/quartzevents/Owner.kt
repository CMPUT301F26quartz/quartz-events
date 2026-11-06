package com.example.quartzevents

class Owner(
    email: String = "",
    name: String = "",
    uid: String = ""
) : User(email, name, role = "owner", uid = uid) {
    val campsites: MutableList<Campsite> = mutableListOf()
    val messages: MutableList<Message> = mutableListOf()
}