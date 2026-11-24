package com.example.quartzevents.Classes

class DirectMessage(
    senderId: String = "",
    receiverId: String = "",
    message: String = ""
): Message(type = "direct", senderId, receiverId, message){

}