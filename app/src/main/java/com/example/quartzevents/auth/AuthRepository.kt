package com.example.quartzevents.auth

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    //Register a new user with email and passwor
    fun register(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.message)
            }
    }

    //Log in an existing user
    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.message)
            }

    }

    //Get the currently logged-in user's UID
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    //Logout
    fun logout() {
        auth.signOut()
    }
}