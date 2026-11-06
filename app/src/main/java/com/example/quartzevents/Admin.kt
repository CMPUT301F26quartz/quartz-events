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

package com.example.quartzevents

class Admin(
    email: String = "",
    name: String = "",
    uid: String = ""
) : User(email, name, role = "admin", uid = uid) {
    val messages: MutableList<Message> = mutableListOf()
}