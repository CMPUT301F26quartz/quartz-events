package com.example.quartzevents.auth

import com.google.firebase.auth.FirebaseAuth
import com.example.quartzevents.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    //Register a new user with email and passwor
    fun register(
        name: String,
        email: String,
        password: String,
        role: String,
        username: String,
        phoneNumber: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        // Only entrants and campsite owners can register
        if (role != "entrant" && role != "owner") {
            onResult(false, "Invalid registration role")
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if(uid == null) {
                    onResult(false, "Could not get user ID")
                    return@addOnSuccessListener
                }
                val profile = UserProfile(
                    uid = uid,
                    name = name,
                    email = email,
                    role = role,
                    username = username,
                    phoneNumber = phoneNumber
                )
                saveUserProfile(profile) { success, error ->
                    onResult(success, error)
                }
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
    //Save a user's profile in Firestore
    fun saveUserProfile(
        profile: UserProfile,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection("users")
            .document(profile.uid)
            .set(profile)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.message)
            }
    }

    fun getUserProfile(
        uid: String,
        onResult: (UserProfile?, String?) -> Unit
    ) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val profile = document.toObject(UserProfile::class.java)
                onResult(profile, null)
            }
            .addOnFailureListener { exception ->
                onResult(null, exception.message)
            }
    }
}