/**
 * Represents an Owner user, stores the user's information.
 *
 * An Owner has a name, email, and user id. Entrant also keeps track of their
 * campsites and messages.
 *
 * @property name the name of the user
 * @property email the user's email address
 * @property uid the user's id
 */

package com.example.quartzevents.Classes

class Owner(
    email: String = "",
    name: String = "",
    uid: String = "",
    deviceLogin: Boolean = false,
    deviceId: List<String> = emptyList()
) : User(email, name, role = "owner", uid = uid, deviceLogin=deviceLogin, deviceId = deviceId) {
    val campsites: MutableList<Campsite> = mutableListOf()
    val messages: MutableList<Message> = mutableListOf()
}