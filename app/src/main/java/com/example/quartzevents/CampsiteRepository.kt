//TODO: add description

package com.example.quartzevents

import com.google.firebase.firestore.FirebaseFirestore

class CampsiteRepository {
    private val db = FirebaseFirestore.getInstance()
    fun getAllCampsites(
        onResult: (List<Campsite>?, String?) -> Unit
    ) {
        db.collection("campsites")
            .get()
            .addOnSuccessListener { result ->
                val campsites = result.toObjects(Campsite::class.java)
                onResult(campsites, null)
            }
            .addOnFailureListener { exception ->
                onResult(null, exception.message)
            }
    }

}