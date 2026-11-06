/**
 * Represents a user, and stores the user's information.
 *
 * A user has a name, email, role, and user id. All entrants, owners,
 * and admin are users
 *
 * @property name the name of the user
 * @property email the user's email address
 * @property role the user's role (entrant, owner, or admin)
 * @property uid the user's id
 */

package com.example.quartzevents

abstract class User(
    var email: String = "",
    var name: String = "",
    var role: String = "",
    var uid: String = ""
){}