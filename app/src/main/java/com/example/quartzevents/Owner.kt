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

package com.example.quartzevents

class Owner(
    email: String = "",
    name: String = "",
    uid: String = ""
) : User(email, name, role = "owner", uid = uid) {
    val campsites: MutableList<Campsite> = mutableListOf()
    val messages: MutableList<Message> = mutableListOf()
}