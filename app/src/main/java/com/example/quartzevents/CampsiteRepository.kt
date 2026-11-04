package com.example.quartzevents

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CampsiteRepository {
    private val _campsites = mutableStateListOf<Campsite>()
    val campsites: List<Campsite>
        get()=_campsites

    init{
        Firebase.firestore.collection("campsites")
            .addSnapshotListener {
                snapshot, error ->  if(error!=null || snapshot==null){
                    return@addSnapshotListener
                }

                _campsites.clear()

                snapshot?.documents?.forEach {document ->
                    val campsite = document.toObject(Campsite::class.java)
                    if(campsite!=null){
                        _campsites.add(campsite)
                    }
                }
            }
    }

}