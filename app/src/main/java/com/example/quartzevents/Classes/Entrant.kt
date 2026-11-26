/**
 * Represents an Entrant user, stores the user's information.
 *
 * An Entrant has a name, email, and user id. Entrant also keeps track of the user's
 * reservations, notifications, and messages.
 *
 * @property name the name of the user
 * @property email the user's email address
 * @property uid the user's id
 */

package com.example.quartzevents.Classes

class Entrant(
    email: String = "",
    name: String = "",
    uid: String = "",
    deviceLogin: Boolean = false,
    deviceId: List<String> = emptyList()
) : User(email, name, role = "entrant", uid = uid, deviceLogin=deviceLogin, deviceId = deviceId) {

    val reservations: MutableList<Reservation> = mutableListOf()
    var notificationsOn = true
    val messages: MutableList<Message> = mutableListOf()
}