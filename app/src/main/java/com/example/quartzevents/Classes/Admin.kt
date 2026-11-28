package com.example.quartzevents.Classes

/**
 * Represents an Admin user, stores the user's information.
 *
 * An Admin has a name, email, and user id. Admin also keeps track of their messages
 * through a mutable list of messages
 *
 * @property name the name of the user
 * @property email the user's email address
 * @property uid the user's id
 */
class Admin(
    email: String = "",
    name: String = "",
    uid: String = "",
    deviceLogin: Boolean = false,
    deviceId: List<String> = emptyList(),
    password:String = ""
) : User(email, name, role = "admin", uid = uid, deviceLogin=deviceLogin, deviceId = deviceId, password) {
    val messages: MutableList<Message> = mutableListOf()
}