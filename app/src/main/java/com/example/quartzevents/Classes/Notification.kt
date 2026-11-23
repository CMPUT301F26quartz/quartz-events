package com.example.quartzevents.Classes

abstract class Notification(
    senderId: String = "",
    receiverId: String = "",
    message: String = ""
): Message(type = "notification", senderId, receiverId, message){

}