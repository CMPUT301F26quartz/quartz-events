package com.example.quartzevents.OwnerFunctions


import com.example.quartzevents.OwnerFunctions.Campsite
import com.google.firebase.firestore.FirebaseFirestore


//updating and pulling from the database happens here. handles database interactions between owners and firebase
class CampsiteRepository {
    private val db = FirebaseFirestore.getInstance()

    fun saveCampsite(
        campsite: Campsite,
        onResult: (Boolean, String?) -> Unit
    ) {
        val newCampsiteRef = db.collection("campsites").document()

        val campsiteToSave = campsite.copy(id = newCampsiteRef.id)

        newCampsiteRef.set(campsiteToSave)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.message)
            }
    }


        // gets every campsite listed to the current logged in owner profile, can be reworked for admin too.
        fun getOwnerCampsites(
            ownerId: String,
            onResult: (List<Campsite>?, String?) -> Unit
        ) {
            db.collection("campsites")
                .whereEqualTo("ownerId", ownerId)
                .get()
                .addOnSuccessListener { result ->
                    val campsites = result.toObjects(Campsite::class.java)
                    onResult(campsites, null)
                }
                .addOnFailureListener { exception ->
                    onResult(null, exception.message)
                }
        }

    fun getCampsiteById(campsiteId: String, onResult: (Campsite?, String?) -> Unit) {
        db.collection("campsites").document(campsiteId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.toObject(Campsite::class.java), null)
                } else {
                    onResult(null, "Not found")
                }
            }
            .addOnFailureListener { onResult(null, it.message) }
    }
    }
