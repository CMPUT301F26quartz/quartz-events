package com.example.quartzevents.Classes

abstract class Message(
    var type: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = ""
){}