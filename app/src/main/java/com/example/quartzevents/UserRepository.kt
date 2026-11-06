//TODO: add description

package com.example.quartzevents

import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    //provides all users
    fun getAllUsers(
        onResult: (List<User>?, String?) -> Unit
    ) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val users = mutableListOf<User>()
                for(document in result.documents) {
                    val user: User? = when (document.getString("role")) {
                        "admin"   -> document.toObject(Admin::class.java)
                        "owner"   -> document.toObject(Owner::class.java)
                        "entrant" -> document.toObject(Entrant::class.java)
                        else      -> null
                    }
                    if (user != null) users.add(user)
                }
                onResult(users, null)
            }
            .addOnFailureListener { exception ->
                onResult(null, exception.message)
            }
    }

    //provides all admin users
    fun getAllAdmin(
        onResult: (List<Admin>?, String?) -> Unit
    ) {
        db.collection("users")
            .whereEqualTo("role", "admin")
            .get()
            .addOnSuccessListener { result ->
                val admin = result.toObjects(Admin::class.java)
                onResult(admin, null)
            }
            .addOnFailureListener { exception ->
                onResult(null, exception.message)
            }
    }

    //provides all entrant users
    fun getAllEntrants(
        onResult: (List<Entrant>?, String?) -> Unit
    ) {
        db.collection("users")
            .whereEqualTo("role", "entrant")
            .get()
            .addOnSuccessListener { result ->
                val entrants = result.toObjects(Entrant::class.java)
                onResult(entrants, null)
            }
            .addOnFailureListener { exception ->
                onResult(null, exception.message)
            }
    }

    //provides all owner users
    fun getAllOwners(
        onResult: (List<Owner>?, String?) -> Unit
    ) {
        db.collection("users")
            .whereEqualTo("role", "owner")
            .get()
            .addOnSuccessListener { result ->
                val owners = result.toObjects(Owner::class.java)
                onResult(owners, null)
            }
            .addOnFailureListener { exception ->
                onResult(null, exception.message)
            }
    }

}