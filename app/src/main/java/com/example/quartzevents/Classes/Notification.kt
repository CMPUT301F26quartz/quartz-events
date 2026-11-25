package com.example.quartzevents.Classes

class Notification(
    senderId: String = "",
    receiverId: String = "",
    message: String = ""
): Message(type = "notification", senderId, receiverId, message){

}