package com.example.quartzevents

class Admin(
    email: String = "",
    name: String = "",
    uid: String = ""
) : User(email, name, role = "admin", uid = uid) {
    val messages: MutableList<Message> = mutableListOf()
}