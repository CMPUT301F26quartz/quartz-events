package com.example.quartzevents.Repositories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.quartzevents.Classes.Notification
import com.example.quartzevents.Classes.DirectMessage
import com.example.quartzevents.Classes.Message
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class MessageRepo {
    private val db = Firebase.firestore
    private val messagesRef = db.collection("messages")
    private val _messages = mutableStateListOf<Message>()

    var isLoading by mutableStateOf(true)
        private set
    /** The most recent list-loading error, or null when the listener succeeds. */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val listener: ListenerRegistration = messagesRef.addSnapshotListener { snapshot, error ->
        isLoading = false
        if (error != null) {
            errorMessage = "Couldn't load messages. Please retry."
            return@addSnapshotListener
        }

        errorMessage = null
//        _messages.clear()
        snapshot?.documents?.forEach { document ->
            val message: Message? = when (document.getString("type")) {
                "notification"   -> document.toObject(Notification::class.java)
                "direct"   -> document.toObject(DirectMessage::class.java)
                else      -> null
            }
            if (message != null) _messages.add(message)
        }
    }

    /** Removes the live list listener when its owning page leaves the composition. */
    fun stopListening() {
        listener.remove()
    }

    fun getAllMessages():List<Message>{
        return _messages
    }

    @Suppress("UNCHECKED_CAST")
    fun getAllNotifications():List<Notification>{
        return _messages.filter{it.type == "notification"} as List<Notification>
    }

    @Suppress("UNCHECKED_CAST")
    fun getAllDirectMessages():List<DirectMessage>{
        return _messages.filter{it.type == "direct"} as List<DirectMessage>
    }

    @Suppress("UNCHECKED_CAST")
    fun getAllMessagesBetween(user1: String, user2: String): List<DirectMessage>{
        return _messages.filter{it.type == "direct" &&
                ( (it.senderId==user1 && it.receiverId==user2) ||
                        (it.senderId==user2 &&it.senderId==user1) )} as List<DirectMessage>
    }
}