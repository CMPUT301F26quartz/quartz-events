package com.example.quartzevents

abstract class Message(val sender: User, val receiver: User, val message: String) {
}