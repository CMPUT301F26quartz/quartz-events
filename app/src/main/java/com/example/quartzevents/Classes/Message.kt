package com.example.quartzevents.Classes

open class Message(
    var type: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = ""
){}