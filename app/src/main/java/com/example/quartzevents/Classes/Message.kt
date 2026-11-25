package com.example.quartzevents.Classes

open class Message(
    var type: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = ""
) {
    var title: String = ""
    var senderName: String = ""
    var receiverName: String = ""
    var broadcastId: String = ""
    var reservationId: String = ""
    var kind: String = ""
    var sentAt: com.google.firebase.Timestamp? = null

    @get:com.google.firebase.firestore.DocumentId
    var documentId: String = ""
}