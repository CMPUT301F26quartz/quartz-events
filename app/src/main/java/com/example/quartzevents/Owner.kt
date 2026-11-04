package com.example.quartzevents

class Owner(): User(){
    val email: String = ""
    val name: String = ""
    val role: String = ""
    val uid: String = ""

    val campsites: MutableList<Campsite> = mutableListOf()
    val messages: MutableList<Message> = mutableListOf()
}