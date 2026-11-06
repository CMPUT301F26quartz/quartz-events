package com.example.quartzevents.OwnerFunctions


import com.example.quartzevents.Campsite
import com.google.firebase.firestore.FirebaseFirestore


/**
 * Manages all database interactions between the Owner and Firebase.
 *
 * Acts as a middle man for creating and retrieving data.
 * Uses Firestore to store and fetch campsite documents.
 *
 * @constructor Creates a repository instance and connects to Firestore.
 */

class CampsiteRepository {
    private val db = FirebaseFirestore.getInstance()


    /**
     * Saves a new campsite.
     * Generates a unique ID.
     * Sends data to Firestore.
     *
     * @param campsite The data to save.
     * @param onResult Callback for success or failure.
     */

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

    /**
     * Retrieves owner's campsites.
     * Filters by owner ID.
     *
     * @param ownerId The owner's UID.
     * @param onResult Callback returning the list.
     */
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


    /**
     * Fetches a single campsite.
     * Uses the document ID.
     *
     * @param campsiteId The document ID.
     * @param onResult Callback returning the campsite.
     */

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
