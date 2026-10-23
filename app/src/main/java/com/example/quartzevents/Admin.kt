package com.example.quartzevents

class Admin(email: String, name: String, password: String): User(email, name, password) {
    override fun userType(){
        val type = "admin"
    }

    val messages: MutableList<Message> = mutableListOf()
}