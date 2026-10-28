package com.example.quartzevents

import androidx.compose.animation.core.snap
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CampsiteRepository {
    private val _campsites = MutableStateFlow<List<Campsite>>(emptyList())
    val allCampsites: StateFlow<List<Campsite>> = _campsites.asStateFlow()

    fun update(){
        Firebase.firestore.collection("campsites")
            .addSnapshotListener {
                snapshot, error ->  if(error!=null || snapshot==null){
                    return@addSnapshotListener
                }
                else{
                    _campsites.value = snapshot?.toObjects(Campsite::class.java)!!
                }
            }
    }
}