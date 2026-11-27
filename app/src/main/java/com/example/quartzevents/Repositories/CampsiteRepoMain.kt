package com.example.quartzevents.Repositories

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.quartzevents.Classes.Campsite
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class CampsiteRepoMain {
    private val db = Firebase.firestore
    private val campsitesRef = db.collection("campsites")
    private val _campsites = mutableStateListOf<Campsite>()

    /** Whether the initial campsite list is still loading. */
    var isLoading by mutableStateOf(true)
        private set
    /** The most recent list-loading error, or null when the listener succeeds. */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val listener: ListenerRegistration = campsitesRef.addSnapshotListener { snapshot, error ->
        isLoading = false
        if (error != null) {
            errorMessage = "Couldn't load campsites. Please retry."
            return@addSnapshotListener
        }

        errorMessage = null
        _campsites.clear()
        snapshot?.documents?.forEach { document ->
            _campsites.add(document.toObject(Campsite::class.java)!!)
        }
        _campsites.sortBy { it.name.lowercase() }
    }

    /** Removes the live list listener when its owning page leaves the composition. */
    fun stopListening() {
        listener.remove()
    }

    /** Returns the list of all campsites */
    fun getAllCamsites():List<Campsite>{
        return _campsites
    }

    /** Returns a specific campsite using the campsite id */
    fun getCampsite(id:String): Campsite?{
        for (campsite in _campsites){
            if(campsite.id == id) return campsite
        }
        errorMessage = "Could not fine campsite."
        return null
    }

    /** Returns the list of all campsites owned by a particular owner */
    fun getOwnerCampsites(ownerId: String): List<Campsite>{
        return _campsites.filter{it.ownerId == ownerId}
    }

    fun deleteCampsite(id: String){
        db.collection("campsites")
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { result ->
                result.documents.forEach{ doc ->
                    doc.reference.delete()
                }
            }
            .addOnFailureListener { e -> Log.e("Firestore", "Query failed", e) }

    }

}