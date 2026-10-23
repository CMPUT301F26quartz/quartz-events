package com.example.quartzevents

class Owner(email:String, name:String, password:String): User(email, name, password) {
    override fun userType(){
        val type = "owner"
    }
    val campsites: MutableList<Campsite> = mutableListOf()
    val messages: MutableList<Message> = mutableListOf()
}