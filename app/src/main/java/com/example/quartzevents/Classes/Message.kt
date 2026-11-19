package com.example.quartzevents.Classes

abstract class Message(val sender: User, val receiver: User, val message: String) {
}