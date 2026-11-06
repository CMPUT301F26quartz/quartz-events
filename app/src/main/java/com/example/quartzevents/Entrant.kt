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

package com.example.quartzevents

class Entrant(
    email: String = "",
    name: String = "",
    uid: String = ""
) : User(email, name, role = "entrant", uid = uid) {

    val reservations: MutableList<Reservation> = mutableListOf()
    var notificationsOn = true
    val messages: MutableList<Message> = mutableListOf()
}