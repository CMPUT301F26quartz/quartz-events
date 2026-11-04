package com.example.quartzevents

class Owner(): User(){
    val campsites: MutableList<Campsite> = mutableListOf()
    val messages: MutableList<Message> = mutableListOf()
}