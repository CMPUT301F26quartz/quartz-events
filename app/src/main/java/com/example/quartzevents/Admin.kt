package com.example.quartzevents

class Admin(email: String, name: String, password: String): User(email, name, password) {
    override fun userType(): String{
        val type = "admin"
        return type
    }

    val messages: MutableList<Message> = mutableListOf()
}