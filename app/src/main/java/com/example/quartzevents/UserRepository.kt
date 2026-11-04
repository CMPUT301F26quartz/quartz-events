package com.example.quartzevents

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class UserRepository {
    private val _users = mutableStateListOf<User>()
    val users: List<User>
        get()=_users

    private val _admins = mutableStateListOf<User>()
    val admins: List<User>
        get()=_users

    private val _owners = mutableStateListOf<User>()
    val owners: List<User>
        get()=_owners

    private val _entrants = mutableStateListOf<User>()
    val entrants: List<User>
        get()=_entrants

    init{
        Firebase.firestore.collection("users")
            .addSnapshotListener {
                    snapshot, error ->  if(error!=null || snapshot==null){
                return@addSnapshotListener
            }

                _users.clear()
                _admins.clear()
                _owners.clear()
                _entrants.clear()

                snapshot?.documents?.forEach {document ->
                    val role = document.getString("role")
                    var user:User?
                    if(role == "admin"){
                        user = document.toObject(Admin::class.java)
                    }
                    else if(role == "owner"){
                        user = document.toObject(Owner::class.java)
                    }
                    else {
                        user = document.toObject(Entrant::class.java)
                    }
                    if(user!=null){
                        _users.add(user)
                        //adding into list according to role
                        if(user.role == "admin") _admins.add(user)
                        else if(user.role == "owner") _owners.add(user)
                        else if(user.role == "entrant") _entrants.add(user)
                    }
                }
            }
    }
}