package com.example.quartzevents

class Admin(): User() {
    val messages: MutableList<Message> = mutableListOf()
}