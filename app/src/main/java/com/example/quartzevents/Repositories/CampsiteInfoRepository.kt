package com.example.quartzevents.Repositories

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.quartzevents.EntrantFunctions.CampsiteInfo
import com.example.quartzevents.EntrantFunctions.readCampsite
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/**
 * Provides shared Firestore access to campsite lists and individual campsite details.
 * The owning page calls [stopListening] when it leaves the composition.
 */
class CampsiteInfoRepository {
    private val db = Firebase.firestore
    private val campsitesRef = db.collection("campsites")
    private val _campsites = mutableStateListOf<CampsiteInfo>()

    /** The live campsite list, sorted by name and observed by Compose. */
    val campsites: List<CampsiteInfo>
        get() = _campsites

    /** Whether the initial campsite list is still loading. */
    var isLoading by mutableStateOf(true)
        private set
    /** The most recent list-loading error, or null when the listener succeeds. */
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

    companion object {
        /** Reads one campsite without starting a listener for the entire campsite list. */
        suspend fun getCampsite(id: String): CampsiteInfo? {
            val document = Firebase.firestore.collection("campsites").document(id).get(Source.SERVER).await()
            return document.data?.let { readCampsite(document.id, it) }
        }
    }

    /** Removes the live list listener when its owning page leaves the composition. */
    fun stopListening() {
        listener.remove()
    }
}
