package com.example.quartzevents

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore

class CampsiteInfoRepository {
    private val db = Firebase.firestore
    private val campsitesRef = db.collection("campsites")
    private val _campsites = mutableStateListOf<CampsiteInfo>()

    val campsites: List<CampsiteInfo>
        get() = _campsites

    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val listener: ListenerRegistration

    init {
        listener = campsitesRef.addSnapshotListener { snapshot, error ->
            isLoading = false
            if (error != null) {
                errorMessage = "Couldn't load campsites. Please retry."
                return@addSnapshotListener
            }

            errorMessage = null
            _campsites.clear()
            snapshot?.documents?.forEach { document ->
                _campsites.add(readCampsite(document.id, document.data.orEmpty()))
            }
            _campsites.sortBy { it.name.lowercase() }
        }
    }

    fun getCampsite(id: String, onResult: (CampsiteInfo?, String?) -> Unit) {
        campsitesRef.document(id).get(Source.SERVER)
            .addOnSuccessListener { document ->
                val fields = document.data
                val campsite = if (fields == null) null else readCampsite(document.id, fields)
                onResult(campsite, null)
            }
            .addOnFailureListener {
                onResult(null, "Couldn't load this campsite. Please retry.")
            }
    }

    fun stopListening() {
        listener.remove()
    }
}
