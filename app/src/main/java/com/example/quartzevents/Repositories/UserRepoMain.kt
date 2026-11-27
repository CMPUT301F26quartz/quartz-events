//User Repository

package com.example.quartzevents.Repositories

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.quartzevents.Classes.Admin
import com.example.quartzevents.Classes.Entrant
import com.example.quartzevents.Classes.Owner
import com.example.quartzevents.Classes.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class UserRepoMain {
    private val db = Firebase.firestore
    private val usersRef = db.collection("users")
    private val _users = mutableStateListOf<User>()

    var isLoading by mutableStateOf(true)
        private set
    /** The most recent list-loading error, or null when the listener succeeds. */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val listener: ListenerRegistration = usersRef.addSnapshotListener { snapshot, error ->
        isLoading = false
        if (error != null) {
            errorMessage = "Couldn't load users. Please retry."
            return@addSnapshotListener
        }

        errorMessage = null
        _users.clear()
        snapshot?.documents?.forEach { document ->
            val user: User? = when (document.getString("role")) {
                "admin"   -> document.toObject(Admin::class.java)
                "owner"   -> document.toObject(Owner::class.java)
                "entrant" -> document.toObject(Entrant::class.java)
                else      -> null
            }
            if (user != null) _users.add(user)
        }
        _users.sortBy { it.name.lowercase() }
    }

    /** Removes the live list listener when its owning page leaves the composition. */
    fun stopListening() {
        listener.remove()
    }

    fun getAllUsers():List<User>{
        return _users
    }

    @Suppress("UNCHECKED_CAST")
    fun getAllAdmin():List<Admin>{
        return _users.filter{it.role == "admin"} as List<Admin>
    }

    @Suppress("UNCHECKED_CAST")
    fun getAllOwners():List<Owner>{
        return _users.filter{it.role == "owner"} as List<Owner>
    }

    @Suppress("UNCHECKED_CAST")
    fun getAllEntrants():List<Entrant>{
        return _users.filter{it.role == "entrant"} as List<Entrant>
    }
}