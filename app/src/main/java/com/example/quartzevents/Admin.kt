package com.example.quartzevents

class Admin(): User() {
    val email: String = ""
    val name: String = ""
    val role: String = ""
    val uid: String = ""

    val messages: MutableList<Message> = mutableListOf()
}